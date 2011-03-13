package chameleon.aspects.advice.types.weaving.methodInvocation;

import java.util.List;

import jnome.core.expression.ArrayCreationExpression;
import jnome.core.expression.ArrayInitializer;
import jnome.core.expression.ClassLiteral;
import jnome.core.language.Java;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.RegularJavaType;

import org.rejuse.logic.ternary.Ternary;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.Literal;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.VariableReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.statement.Statement;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.NullLiteral;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.expression.ThisLiteral;
import chameleon.support.member.simplename.SimpleNameMethodInvocation;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import chameleon.support.statement.StatementExpression;

public class DefaultReflectiveMethodInvocation implements AdviceWeaveResultProvider<MethodInvocation, MethodInvocation> {

	@Override
	public MethodInvocation getWeaveResult(Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> matchResult) throws LookupException {
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		NamingRegistry<Method> namingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("javamethod");
		
		// Create a call to the advice method
		Method method = matchResult.getJoinpoint().getElement();
		RegularMethodInvocation getInstance = new RegularMethodInvocation("instance", new NamedTarget(advice.aspect().name()));
		RegularMethodInvocation adviceInvocation = new RegularMethodInvocation("advice_" + adviceNamingRegistry.getName(advice) + "_" + namingRegistry.getName(method), getInstance);
		Statement call = new StatementExpression(adviceInvocation);
		
		InvocationTarget target = matchResult.getJoinpoint().getTarget();
		if (target == null)
			target = new ThisLiteral();
		else {
			if (target instanceof NamedTarget && ((NamedTarget) target).getElement() instanceof RegularJavaType) {
				target = new ClassLiteral(new BasicTypeReference(((RegularJavaType) ((NamedTarget) target).getElement()).getType().getFullyQualifiedName()));
			} else {
				target = target.clone();
			}
		}

		adviceInvocation.addArgument(new VariableReference("object", target));
		adviceInvocation.addArgument(new RegularLiteral(new BasicTypeReference("String"), "\"" + ((SimpleNameMethodInvocation)matchResult.getJoinpoint()).name()+ "\""));
		List<Expression> methodParameters = matchResult.getJoinpoint().getActualParameters();
		ArrayCreationExpression parameterArray = new ArrayCreationExpression(new ArrayTypeReference(new BasicJavaTypeReference("Object")));
		ArrayInitializer parameterInitializer = new ArrayInitializer();					
	
		for (Expression e : methodParameters)
			parameterInitializer.addInitializer(e.clone());
		
		parameterArray.setInitializer(parameterInitializer);
		
		adviceInvocation.addArgument(parameterArray);
		
		ArrayCreationExpression indexArray = new ArrayCreationExpression(new ArrayTypeReference(new BasicJavaTypeReference("int")));
		ArrayInitializer indexInitializer = new ArrayInitializer();
		
		Pointcut pc = advice.pointcut();
		for (FormalParameter param : (List<FormalParameter>) pc.header().formalParameters()) {
			// Find the index of the parameter with the same name as 'param' in the matched pointcut ref
			int index = matchResult.getExpression().indexOfParameter(param);
			
			indexInitializer.addInitializer(new RegularLiteral(new BasicJavaTypeReference("int"), Integer.toString(index)));
		}
		
		indexArray.setInitializer(indexInitializer);
		adviceInvocation.addArgument(indexArray);	
		
		// A method invocation is either contained in a method, or in a local member declaration
		// Note, we can not use 'ElementWithModifier', e.g. in this scenario: static void foo() { final int i = methodInvo(); } would match the local variable 
		Element currentElement = matchResult.getJoinpoint();
		boolean found = false;
		boolean isStatic = false;
		while (!found && currentElement.parent() != null) {
			currentElement = currentElement.parent();
			if (currentElement instanceof MemberVariableDeclarator) {
				found = true;
				isStatic = (((MemberVariableDeclarator) currentElement).isTrue(currentElement.language().property("class")));
			} else if (currentElement instanceof Method) {
				found = true;
				isStatic = (((Method) currentElement).isTrue(currentElement.language().property("class")));
			}
		}
		
		Literal self;
		if (isStatic)
			self = new NullLiteral();
		else
			self = new ThisLiteral();
		
		adviceInvocation.addArgument(self);

		// Set the generic parameter
		Type type = matchResult.getJoinpoint().getElement().returnType();
		Java java = (Java) matchResult.getJoinpoint().language(Java.class);
				
		// Set the generic parameter
		if (matchResult.getJoinpoint().getType() != ((ObjectOrientedLanguage) matchResult.getJoinpoint().language(ObjectOrientedLanguage.class)).voidType()) {
			if (type.is(java.PRIMITIVE_TYPE) == Ternary.TRUE)
				type = java.box(type);
			
			adviceInvocation.addArgument(new BasicTypeArgument(new BasicTypeReference(type.getFullyQualifiedName())));
		}
		
		return adviceInvocation;
	}
}

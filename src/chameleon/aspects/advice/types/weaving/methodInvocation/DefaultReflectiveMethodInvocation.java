package chameleon.aspects.advice.types.weaving.methodInvocation;

import java.util.List;

import jnome.core.expression.ArrayCreationExpression;
import jnome.core.expression.ArrayInitializer;
import jnome.core.expression.ClassLiteral;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.type.RegularJavaType;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.VariableReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.statement.Statement;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.expression.ThisLiteral;
import chameleon.support.member.simplename.SimpleNameMethodInvocation;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.StatementExpression;

public class DefaultReflectiveMethodInvocation implements AdviceWeaveResultProvider<MethodInvocation, MethodInvocation> {

	@Override
	public MethodInvocation getWeaveResult(CompilationUnit compilationUnit,	Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> matchResult) throws LookupException {
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		NamingRegistry<Method> namingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("javamethod");
		
		// Create a call to the advice method
		RegularMethodInvocation adviceInvocation = new RegularMethodInvocation("advice_" + adviceNamingRegistry.getName(advice) + "_" + namingRegistry.getName(matchResult.getJoinpoint().getElement()), new NamedTarget(advice.aspect().name()));
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
		
		// Set the generic parameter
		if (!matchResult.getJoinpoint().getType().signature().name().equals("void"))
			adviceInvocation.addArgument(new BasicTypeArgument(new BasicTypeReference(matchResult.getJoinpoint().getType().getFullyQualifiedName())));
		
		return adviceInvocation;
	}

}

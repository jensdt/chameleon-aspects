package chameleon.aspects.advice.types.weaving.methodInvocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.expression.ArrayCreationExpression;
import jnome.core.expression.ArrayInitializer;
import jnome.core.language.Java;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;

import org.rejuse.logic.ternary.Ternary;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.weaving.ReflectiveProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.Literal;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.VariableReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.RegularLiteral;

public class DefaultReflectiveMethodInvocation extends ReflectiveProvider<MethodInvocation> {

	@Override
	protected String getName(Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) throws LookupException {
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		NamingRegistry<Method> methodNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("javamethod");
		
		Method method = joinpoint.getJoinpoint().getElement();
		
		return "advice_" + adviceNamingRegistry.getName(advice) + "_" + methodNamingRegistry.getName(method);
	}
	
	@Override
	protected List<Expression> getParameters(Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) throws LookupException {
		List<Expression> parameters = new ArrayList<Expression>();
		
		InvocationTarget target = getTarget(joinpoint);

		parameters.add(new VariableReference("object", target));
		parameters.add(new RegularLiteral(new BasicTypeReference("String"), "\"" + joinpoint.getJoinpoint().getElement().name() + "\""));
		
		List<Expression> methodParameters = joinpoint.getJoinpoint().getActualParameters();
		ArrayCreationExpression parameterArray = new ArrayCreationExpression(new ArrayTypeReference(new BasicJavaTypeReference("Object")));
		ArrayInitializer parameterInitializer = new ArrayInitializer();					
	
		for (Expression e : methodParameters)
			parameterInitializer.addInitializer(e.clone());
		
		parameterArray.setInitializer(parameterInitializer);
		
		parameters.add(parameterArray);
		
		Expression self = getSelf(joinpoint);
		
		parameters.add(self);
		
		return parameters;
	}

	@Override
	protected BasicTypeArgument getGenericParameter(Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint)	throws LookupException {
		Type type = joinpoint.getJoinpoint().getElement().returnType();
		Java java = (Java) joinpoint.getJoinpoint().language(Java.class);
				
		// Set the generic parameter
		if (joinpoint.getJoinpoint().getType() != ((ObjectOrientedLanguage) joinpoint.getJoinpoint().language(ObjectOrientedLanguage.class)).voidType()) {
			if (type.is(java.PRIMITIVE_TYPE) == Ternary.TRUE)
				type = java.box(type);
			
			return new BasicTypeArgument(new BasicTypeReference(type.getFullyQualifiedName()));
		}
		
		return null;
	}
}
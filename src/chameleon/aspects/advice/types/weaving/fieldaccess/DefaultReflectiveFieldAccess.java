package chameleon.aspects.advice.types.weaving.fieldaccess;

import java.util.ArrayList;
import java.util.List;

import jnome.core.language.Java;

import org.rejuse.logic.ternary.Ternary;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.weaving.ReflectiveProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.Literal;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.expression.VariableReference;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.RegularLiteral;

public class DefaultReflectiveFieldAccess extends ReflectiveProvider<NamedTargetExpression> {

	@Override
	protected String getName(Advice advice, MatchResult<? extends PointcutExpression, ? extends NamedTargetExpression> joinpoint) {
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		
		return "advice_" + adviceNamingRegistry.getName(advice);
	}
	
	@Override
	protected List<Expression> getParameters(Advice advice, MatchResult<? extends PointcutExpression, ? extends NamedTargetExpression> joinpoint) throws LookupException {
		List<Expression> parameters = new ArrayList<Expression>();
		
		InvocationTarget target = getTarget(joinpoint);

		parameters.add(new VariableReference("object", target));
		parameters.add(new RegularLiteral(new BasicTypeReference("String"), "\"" + joinpoint.getJoinpoint().signature().name()+ "\""));

		Expression self = getSelf(joinpoint);
		
		parameters.add(self);
		
		return parameters;
	}

	@Override
	protected BasicTypeArgument getGenericParameter(Advice advice,	MatchResult<? extends PointcutExpression, ? extends NamedTargetExpression> joinpoint) throws LookupException {
		Type type = joinpoint.getJoinpoint().getElement().declarationType();
		Java java = joinpoint.getJoinpoint().language(Java.class);
		
		if (type.isTrue(java.property("primitive")))
			type = java.box(type);
		
		return new BasicTypeArgument(new BasicTypeReference(type.getFullyQualifiedName()));		
	}
}
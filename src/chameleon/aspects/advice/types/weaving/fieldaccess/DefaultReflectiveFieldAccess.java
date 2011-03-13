package chameleon.aspects.advice.types.weaving.fieldaccess;

import org.rejuse.logic.ternary.Ternary;

import jnome.core.expression.ClassLiteral;
import jnome.core.language.Java;
import jnome.core.type.RegularJavaType;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.expression.VariableReference;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Statement;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.expression.ThisLiteral;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.StatementExpression;

public class DefaultReflectiveFieldAccess implements AdviceWeaveResultProvider<NamedTargetExpression, MethodInvocation> {

	@Override
	public MethodInvocation getWeaveResult(Advice advice, MatchResult<? extends PointcutExpression, ? extends NamedTargetExpression> joinpoint)	throws LookupException {
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		
		// Create a call to the advice method
		RegularMethodInvocation getInstance = new RegularMethodInvocation("instance", new NamedTarget(advice.aspect().name()));
		RegularMethodInvocation adviceInvocation = new RegularMethodInvocation("advice_" + adviceNamingRegistry.getName(advice), getInstance);
		Statement call = new StatementExpression(adviceInvocation);
		
		InvocationTarget target = joinpoint.getJoinpoint().getTarget();
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
		adviceInvocation.addArgument(new RegularLiteral(new BasicTypeReference("String"), "\"" + joinpoint.getJoinpoint().signature().name()+ "\""));

		// Set the generic parameter
		Type type = joinpoint.getJoinpoint().getElement().declarationType();
		Java java = joinpoint.getJoinpoint().language(Java.class);
		
		if (type.is(java.PRIMITIVE_TYPE) == Ternary.TRUE)
			type = java.box(type);
		
		adviceInvocation.addArgument(new BasicTypeArgument(new BasicTypeReference(type.getFullyQualifiedName())));
		
		return adviceInvocation;
	}

}

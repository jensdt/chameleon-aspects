package chameleon.aspects.advice.types.weaving;

import java.util.List;

import jnome.core.expression.ClassLiteral;
import jnome.core.type.RegularJavaType;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.Literal;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.TargetedExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.statement.Statement;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.NullLiteral;
import chameleon.support.expression.ThisLiteral;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import chameleon.support.statement.StatementExpression;

public abstract class ReflectiveProvider<T extends TargetedExpression> implements AdviceWeaveResultProvider<T, MethodInvocation> {

	@Override
	public MethodInvocation getWeaveResult(Advice advice, MatchResult<? extends PointcutExpression, ? extends T> joinpoint)	throws LookupException {
		RegularMethodInvocation getInstance = new RegularMethodInvocation("instance", new NamedTarget(advice.aspect().name()));
		RegularMethodInvocation adviceInvocation = new RegularMethodInvocation(getName(advice, joinpoint), getInstance);
		Statement call = new StatementExpression(adviceInvocation);
		
		for (Expression e : getParameters(advice, joinpoint))
			adviceInvocation.addArgument(e);
		
		BasicTypeArgument genericParameter = getGenericParameter(advice, joinpoint);
		if (genericParameter != null)
			adviceInvocation.addArgument(genericParameter);
		
		return adviceInvocation;
	}
	
	
	protected abstract String getName(Advice advice, MatchResult<? extends PointcutExpression, ? extends T> joinpoint) throws LookupException;
	protected abstract List<Expression> getParameters(Advice advice, MatchResult<? extends PointcutExpression, ? extends T> joinpoint) throws LookupException ;
	protected abstract BasicTypeArgument getGenericParameter(Advice advice, MatchResult<? extends PointcutExpression, ? extends T> joinpoint) throws LookupException ;

	protected InvocationTarget getTarget(MatchResult<? extends PointcutExpression, ? extends T> joinpoint)	throws LookupException {
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
		return target;
	}
	
	protected Expression getSelf(MatchResult<? extends PointcutExpression, ? extends T> joinpoint) {
		// Note, we can not use 'ElementWithModifier', e.g. in this scenario: static void foo() { final int i = p.a; } would match the local variable 
		Element currentElement = joinpoint.getJoinpoint();
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
		
		Expression self;
		if (isStatic)
			self = new NullLiteral();
		else
			self = new ThisLiteral(); // new RegularMethodInvocation("getClass", null);
		
		return self;
	}
}
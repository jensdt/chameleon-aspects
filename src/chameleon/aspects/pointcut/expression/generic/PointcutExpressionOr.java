package chameleon.aspects.pointcut.expression.generic;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.dynamicexpression.ParameterExposurePointcutExpression;
import chameleon.aspects.pointcut.expression.staticexpression.StaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionOr<E extends PointcutExpressionOr<E>> extends PointcutExpressionDual<E> implements RuntimePointcutExpression<E>, StaticPointcutExpression<E>, ParameterExposurePointcutExpression<E> {

	public PointcutExpressionOr(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	We can't just return a single expression if it matches - e.g. this example:
 	 * 	(callAnnotated(Deprecated) && if(false) ) || (call(void hrm.Person.doubleTest()) && if(true)) 
	 * 
	 *  Suppose both static pointcut expressions match and we only return the first one - this will cause the weaver not to weave, which is wrong		
	 */
	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		MatchResult r1 = MatchResult.noMatch();
		MatchResult r2 = MatchResult.noMatch();
		
		if (!(expression1() instanceof StaticPointcutExpression))
			r1 = new MatchResult(expression1(), joinpoint);
		else if (((StaticPointcutExpression) expression1()).isSupported(joinpoint.getClass()))
			r1 = ((StaticPointcutExpression) expression1()).matches(joinpoint);
		
		if (!(expression2() instanceof StaticPointcutExpression))
			r2 = new MatchResult(expression1(), joinpoint);
		else if (((StaticPointcutExpression) expression2()).isSupported(joinpoint.getClass()))
			r2 = ((StaticPointcutExpression) expression2()).matches(joinpoint);
		
		if (r1.isMatch() && r2.isMatch())
			return new MatchResult<Element<?>>(this, joinpoint);
		else if (r1.isMatch())
			return r1;
		else if (r2.isMatch())
			return r2;
		else
			return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionOr<E>(expression1().clone(), expression2().clone());
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	A joinpoint is only supported, if it is supported by one of the branches
	 */
	@Override
	public boolean isSupported(Class c) {
		return (!(expression1() instanceof StaticPointcutExpression) || ((StaticPointcutExpression) expression1()).isSupported(c))
		|| (!(expression2() instanceof StaticPointcutExpression) || ((StaticPointcutExpression) expression2()).isSupported(c));
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> getPrunedTree(SafePredicate<PointcutExpression<?>> filter) {	
		PointcutExpression<?> left = expression1().getPrunedTree(filter);
		PointcutExpression<?> right = expression2().getPrunedTree(filter);
		
		if (left == null && right == null)
			return null;
		if (left == null && right != null)
			return right;
		if (left != null && right == null)
			return left;
		
		return new PointcutExpressionOr<E>(left, right);
	}
	
	/**
	 * 	{@inheritDoc}
	 */	
	@Override
	public PointcutExpression<?> removeFromTree(SafePredicate<PointcutExpression<?>> filter) {	
		PointcutExpression<?> left = expression1().removeFromTree(filter);
		PointcutExpression<?> right = expression2().removeFromTree(filter);
		
		if (left == null && right == null)
			return null;
		if (left == null && right != null)
			return right;
		if (left != null && right == null)
			return left;
		
		return new PointcutExpressionOr<E>(left, right);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		return new PointcutExpressionOr<E>(expression1().expand(), expression2().expand());
	}
}
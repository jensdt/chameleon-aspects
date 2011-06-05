package chameleon.aspects.pointcut.expression.generic;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.staticexpression.StaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionAnd<E extends PointcutExpressionAnd<E>> extends PointcutExpressionDual<E> {

	public PointcutExpressionAnd(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		boolean r1match = !(expression1() instanceof StaticPointcutExpression) ||  ((StaticPointcutExpression) expression1()).matches(joinpoint).isMatch();
		boolean r2match = !(expression2() instanceof StaticPointcutExpression) ||  ((StaticPointcutExpression) expression2()).matches(joinpoint).isMatch();
		
		if (r1match && r2match)
			return new MatchResult<Element>(this, joinpoint);
		else
			return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionAnd<E>(expression1().clone(), expression2().clone());
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	A joinpoint is only supported, if it is supported by both branches
	 */
	@Override
	public boolean isSupported(Class c) {
		return (!(expression1() instanceof StaticPointcutExpression) || ((StaticPointcutExpression) expression1()).isSupported(c))
			&& (!(expression2() instanceof StaticPointcutExpression) || ((StaticPointcutExpression) expression2()).isSupported(c));
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
		
		return new PointcutExpressionAnd<E>(left, right);
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
		
		return new PointcutExpressionAnd<E>(left, right);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		return new PointcutExpressionAnd<E>(expression1().expand(), expression2().expand());
	}
}
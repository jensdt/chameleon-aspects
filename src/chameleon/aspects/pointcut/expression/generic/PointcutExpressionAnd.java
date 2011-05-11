package chameleon.aspects.pointcut.expression.generic;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.dynamicexpression.ParameterExposurePointcutExpression;
import chameleon.aspects.pointcut.expression.staticexpression.StaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

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
	 * 	Note that, due to class hierarchy, this isn't a simple intersection. Suppose the following class hierarchy:
	 * 
	 * 	A <- B
	 * 
	 * 	Consider the following cases:
	 * 	Expr1: {A}
	 * 	Expr2: {A}
	 * 
	 * 	=> Result {A} (note: not {A, A})
	 * 
	 * 	Expr1: {B}
	 * 	Expr2: {A}
	 * 
	 * 	=> Result {B}
	 * 
	 * 	Expr1: {A}
	 * 	Expr2: {B}
	 * 
	 *  => Result {B}
	 */
	@Override
	public Set<Class<? extends Element>> supportedJoinpoints() {
		Set<Class<? extends Element>> supported1 = expression1().supportedJoinpoints();
		
		Set<Class<? extends Element>> supportedJoinpoints = new HashSet<Class<? extends Element>>();
		
		for (Class<? extends Element> c : supported1)
			if (expression2().isSupported(c))
				supportedJoinpoints.add(c);
		
		Set<Class<? extends Element>> supported2 = expression2().supportedJoinpoints();
		
		for (Class<? extends Element> c : supported2)
			if (!supportedJoinpoints.contains(c) && expression1().isSupported(c))
				supportedJoinpoints.add(c);
		
		return supportedJoinpoints;
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
	
	@Override
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type) {
		PointcutExpression left = expression1().removeFromTree(type);
		PointcutExpression right = expression2().removeFromTree(type);
		
		if (left == null && right == null)
			return null;
		if (left == null && right != null)
			return right;
		if (left != null && right == null)
			return left;
		
		return new PointcutExpressionAnd(left, right);
	}

	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		// FIXME
		throw new RuntimeException("todo");
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		return new PointcutExpressionAnd<E>(expression1().expand(), expression2().expand());
	}
}
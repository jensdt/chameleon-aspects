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
		MatchResult r1, r2;
		if (!(expression1() instanceof StaticPointcutExpression))
			r1 = new MatchResult(expression1(), joinpoint);
		else
			r1 = ((StaticPointcutExpression) expression1()).matches(joinpoint);
		
		if (!(expression2() instanceof StaticPointcutExpression))
			r2 = new MatchResult(expression1(), joinpoint);
		else
			r2 = ((StaticPointcutExpression) expression2()).matches(joinpoint);
		
		if (r1.isMatch() && r2.isMatch())
			return new MatchResult<PointcutExpressionOr<?>, Element<?>>(this, joinpoint);
		else if (r1.isMatch())
			return r1;
		else if (r2.isMatch())
			return r2;
		else
			return MatchResult.noMatch();
	}
	
	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		// FIXME
		throw new RuntimeException("TODO");
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionOr<E>(expression1().clone(), expression2().clone());
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	Note: this isn't a simple union.
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
	 * 	=> Result {A} (not {A, B} since this will incorrectly match B elements twice!) 
	 * 
	 * 	Expr1: {A}
	 * 	Expr2: {B}
	 * 
	 *  => Result {A}
	 */
	@Override
	public Set<Class<? extends Element>> supportedJoinpoints() {
		Set<Class<? extends Element>> supported1 = expression1().supportedJoinpoints();
		Set<Class<? extends Element>> supported2 = expression2().supportedJoinpoints();
		Set<Class<? extends Element>> supportedJoinpoints = new HashSet<Class<? extends Element>>();
		
		// Add all elements of the first expression that are not supported (equal or subtypes of a type) in expression 2
		for (Class<? extends Element> c : supported1)
			if (!expression2().isSupported(c))
				supportedJoinpoints.add(c);
		
		// Now we need to do the same for the second expression, but if we do, we forget types that are exactly the same!
		for (Class<? extends Element> c : supported2) {
			if (!expression1().isSupported(c))
				supportedJoinpoints.add(c);
			
			if (supported1.contains(c))		// .isSupported would return 'true' here, but we do need to add it
				supportedJoinpoints.add(c);
		}
		
		return supportedJoinpoints;
	}
	
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
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type) {
		PointcutExpression left = expression1().removeFromTree(type);
		PointcutExpression right = expression2().removeFromTree(type);
		
		if (left == null && right == null)
			return null;
		if (left == null && right != null)
			return right;
		if (left != null && right == null)
			return left;
		
		return new PointcutExpressionOr(left, right);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		return new PointcutExpressionOr<E>(expression1().expand(), expression2().expand());
	}
}
package chameleon.aspects.pointcut.expression.generic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionOr<E extends PointcutExpressionOr<E, T>, T extends Element> extends PointcutExpressionDual<E, T> {

	public PointcutExpressionOr(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public MatchResult matches(T joinpoint) throws LookupException {
		MatchResult r1 = expression1().matches(joinpoint);
		
		if (r1.isMatch())
			return r1;
		
		return expression2().matches(joinpoint);
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionOr<E, T>(expression1().clone(), expression2().clone());
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
	public Set<Class> supportedJoinpoints() {
		Set<Class> supported1 = expression1().supportedJoinpoints();
		Set<Class> supported2 = expression2().supportedJoinpoints();
		Set<Class> supportedJoinpoints = new HashSet<Class>();
		
		// Add all elements of the first expression that are not supported (equal or subtypes of a type) in expression 2
		for (Class c : supported1)
			if (!expression2().isSupported(c))
				supportedJoinpoints.add(c);
		
		// Now we need to do the same for the second expression, but if we do, we forget types that are exactly the same!
		for (Class c : supported2) {
			if (!expression1().isSupported(c))
				supportedJoinpoints.add(c);
			
			if (supported1.contains(c))		// .isSupported would return 'true' here, but we do need to add it
				supportedJoinpoints.add(c);
		}
		
		return supportedJoinpoints;
	}
	
	/**
	 * 	{@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return expression1().hasParameter(fp) && expression2().hasParameter(fp);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public int indexOfParameter(FormalParameter fp) {
		throw new RuntimeException();
	}
	
	@Override
	public PointcutExpression getPrunedTree(Class<? extends PointcutExpression> type) {
		PointcutExpression left = expression1().getPrunedTree(type);
		PointcutExpression right = expression2().getPrunedTree(type);
		
		if (left == null && right == null)
			return null;
		if (left == null && right != null)
			return right;
		if (left != null && right == null)
			return left;
		
		return new PointcutExpressionOr(left, right);
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
}

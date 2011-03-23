package chameleon.aspects.pointcut.expression.generic;

import java.util.HashSet;
import java.util.Set;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionAnd<E extends PointcutExpressionAnd<E>> extends PointcutExpressionDual<E> {

	public PointcutExpressionAnd(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		MatchResult r1 = expression1().matches(joinpoint);
		MatchResult r2 = expression2().matches(joinpoint);
		
		if (r1.isMatch() && r2.isMatch())
			return new MatchResult(this, joinpoint);
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
	public Set<Class> supportedJoinpoints() {
		Set<Class> supported1 = expression1().supportedJoinpoints();
		
		Set<Class> supportedJoinpoints = new HashSet<Class>();
		
		for (Class c : supported1)
			if (expression2().isSupported(c))
				supportedJoinpoints.add(c);
		
		Set<Class> supported2 = expression2().supportedJoinpoints();
		
		for (Class c : supported2)
			if (!supportedJoinpoints.contains(c) && expression1().isSupported(c))
				supportedJoinpoints.add(c);
		
		return supportedJoinpoints;
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
		
		return new PointcutExpressionAnd(left, right);
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
		MatchResult r1 = expression1().matchesInverse(joinpoint);
		
		if (r1.isMatch())
			return r1;
		
		return expression2().matchesInverse(joinpoint);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public boolean hasParameter(FormalParameter fp) {
		return expression1().hasParameter(fp) || expression2().hasParameter(fp);
	}
}

package chameleon.aspects.pointcut.expression.generic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionOr<E extends PointcutExpressionOr<E>> extends PointcutExpressionDual<E> {

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
		MatchResult r1 = expression1().matches(joinpoint);
		MatchResult r2 = expression2().matches(joinpoint);
		
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
		MatchResult r1 = expression1().matchesInverse(joinpoint);
		MatchResult r2 = expression2().matchesInverse(joinpoint);
		
		if (r1.isMatch() && r2.isMatch())
			return new MatchResult(this, joinpoint);
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
		
		return new PointcutExpressionOr(left, right);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public boolean hasParameter(FormalParameter fp) {
		return expression1().hasParameter(fp) && expression2().hasParameter(fp);
	}
	
	public int indexOfParameter(FormalParameter fp) {
		return expression1().indexOfParameter(fp);
	}
}

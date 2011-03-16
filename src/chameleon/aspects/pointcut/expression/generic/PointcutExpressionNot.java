package chameleon.aspects.pointcut.expression.generic;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionNot<E extends PointcutExpressionNot<E, T>, T extends Element> extends PointcutExpressionSingle<E, T> {

	public PointcutExpressionNot(PointcutExpression expression) {
		super(expression);
	}

	@Override
	public MatchResult matches(T joinpoint) throws LookupException {
		return expression().matchesInverse(joinpoint);
	}
	
	@Override
	public MatchResult matchesInverse(T joinpoint) throws LookupException {
		return expression().matches(joinpoint);
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionNot<E, T>(expression().clone());
	}
	
	@Override
	public PointcutExpression getPrunedTree(Class<? extends PointcutExpression> type) {
		PointcutExpression expression = expression().getPrunedTree(type);
		
		if (expression == null)
			return null;
		
		return new PointcutExpressionNot(expression);
	}
	
	@Override
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type) {
		PointcutExpression expression = expression().removeFromTree(type);
		
		if (expression == null)
			return null;
		
		return new PointcutExpressionNot(expression);
	}
	
	/**
	 * 	{@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return expression().hasParameter(fp);
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public int indexOfParameter(FormalParameter fp) {
		return expression().indexOfParameter(fp);
	}
}

package chameleon.aspects.pointcut.expression.generic;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionNot<E extends PointcutExpressionNot<E>> extends PointcutExpressionSingle<E> {

	public PointcutExpressionNot(PointcutExpression expression) {
		super(expression);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		return expression().matchesInverse(joinpoint);
	}
	
	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		return expression().matches(joinpoint);
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionNot<E>(expression().clone());
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
}

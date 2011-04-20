package chameleon.aspects.pointcut.expression.generic;

import java.util.Map;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.dynamicexpression.ParameterExposurePointcutExpression;
import chameleon.aspects.pointcut.expression.staticexpression.StaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionNot<E extends PointcutExpressionNot<E>> extends PointcutExpressionSingle<E> implements RuntimePointcutExpression<E>, StaticPointcutExpression<E>, ParameterExposurePointcutExpression<E> {

	public PointcutExpressionNot(PointcutExpression expression) {
		super(expression);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		if (!(expression() instanceof StaticPointcutExpression))
			return new MatchResult(expression(), joinpoint);
		else
			return ((StaticPointcutExpression) expression()).matchesInverse(joinpoint);
	}
	
	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		if (!(expression() instanceof StaticPointcutExpression))
			return new MatchResult(expression(), joinpoint);
		else
			return ((StaticPointcutExpression) expression()).matches(joinpoint);
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionNot<E>(expression().clone());
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> getPrunedTree(SafePredicate<PointcutExpression<?>> filter) {
		PointcutExpression<?> expression = expression().getPrunedTree(filter);
		
		if (expression == null)
			return null;
		
		return new PointcutExpressionNot<E>(expression);
	}
	
	@Override
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type) {
		PointcutExpression expression = expression().removeFromTree(type);
		
		if (expression == null)
			return null;
		
		return new PointcutExpressionNot(expression);
	}

	@Override
	public ParameterExposurePointcutExpression<?> findExpressionFor(FormalParameter fp) {
		if (expression() instanceof ParameterExposurePointcutExpression)
			return ((ParameterExposurePointcutExpression<?>) expression().origin()).findExpressionFor(fp);
		
		return null;
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		return new PointcutExpressionNot<E>(expression().expand());
	}
	
	@Override
	public void renameParameters(Map<String, String> parameterNamesMap) {
		if (expression() instanceof ParameterExposurePointcutExpression)
			((ParameterExposurePointcutExpression<?>) expression()).renameParameters(parameterNamesMap);
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public int indexOfParameter(FormalParameter fp) {
		if (expression() instanceof ParameterExposurePointcutExpression)
			return ((ParameterExposurePointcutExpression<?>) expression()).indexOfParameter(fp);
		
		return -1;
	}
}
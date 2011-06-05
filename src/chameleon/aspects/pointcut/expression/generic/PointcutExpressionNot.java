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
			return new MatchResult(this, joinpoint);
		
		if (!((StaticPointcutExpression) expression()).isSupported(joinpoint.getClass()))
			return new MatchResult(this, joinpoint);
		
		MatchResult matches = ((StaticPointcutExpression) expression()).matches(joinpoint);
		
		if (matches.isMatch())
			return MatchResult.noMatch();
		else
			return new MatchResult(this, joinpoint);
	}
	
	/**
	 * 	{@inheritDoc}
	 * 
	 * 	A not pointcut expression must support everything:
	 * 		* Pointcuts that are of a different type as supported by expression() are supported since they match
	 * 		* Pointcuts that are of the same type as supported by expression(), but for which the matches method fails, are supported since they match
	 */
	@Override
	public boolean isSupported(Class<? extends Element> c) {
		return true;
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
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> removeFromTree(SafePredicate<PointcutExpression<?>> filter) {
		PointcutExpression<?> expression = expression().removeFromTree(filter);
		
		if (expression == null)
			return null;
		
		return new PointcutExpressionNot<E>(expression);
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
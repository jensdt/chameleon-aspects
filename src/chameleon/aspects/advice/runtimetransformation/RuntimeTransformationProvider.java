package chameleon.aspects.advice.runtimetransformation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeParameterExposureProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.dynamicexpression.ParameterExposurePointcutExpression;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;

// FIXME: documentation
public interface RuntimeTransformationProvider<T extends Element> {
	public boolean supports(PointcutExpression<?> pointcutExpression);
	public void initialiseRuntimeTransformers(MatchResult<? extends Element> joinpoint);
	public Coordinator<T> getCoordinator(MatchResult<? extends Element> joinpoint, WeavingEncapsulator previous, WeavingEncapsulator next);
	
	public RuntimeExpressionProvider getRuntimeExpressionProvider(RuntimePointcutExpression pointcutExpression);
	public RuntimeParameterExposureProvider getRuntimeParameterInjectionProvider(ParameterExposurePointcutExpression<?> expression);
}
package chameleon.aspects.weaver.weavingprovider;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.RuntimeTransformationProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.element.Element;

public abstract class AbstractWeavingProviderSupportingRuntimeTransformation<T extends Element, U> extends AbstractWeavingProvider<T, U> implements RuntimeTransformationProvider<T> {
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public final void execute(MatchResult<? extends PointcutExpression, T> joinpoint, U adviceResult, Advice advice, WeavingEncapsulator previous, WeavingEncapsulator next) {
		initialiseRuntimeTransformers(joinpoint);
		Coordinator<T> catchCoordinator = getCoordinator(joinpoint, previous, next);
		
		super.execute(joinpoint, adviceResult, advice, previous, next);
		catchCoordinator.transform(joinpoint.getJoinpoint(), advice.formalParameters());
	}
}

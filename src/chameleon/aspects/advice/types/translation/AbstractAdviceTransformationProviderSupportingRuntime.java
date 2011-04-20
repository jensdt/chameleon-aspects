package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.RuntimeTransformationProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public abstract class AbstractAdviceTransformationProviderSupportingRuntime<T extends Element> extends AbstractAdviceTransformationProvider<T> implements RuntimeTransformationProvider<T> {
	
	public AbstractAdviceTransformationProviderSupportingRuntime(MatchResult joinpoint, Advice advice) {
		super(joinpoint, advice);
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void start(WeavingEncapsulator previousEncapsulator, WeavingEncapsulator nextEncapsulator) throws LookupException {
		T createdElement = transform(nextEncapsulator);

		initialiseRuntimeTransformers(getJoinpoint());
		Coordinator<T> coordinator = getCoordinator(getJoinpoint(), previousEncapsulator, nextEncapsulator);
		if (coordinator != null)
			coordinator.transform(createdElement, getAdvice().formalParameters());
		
		if (next() != null)
			next().start(previousEncapsulator, nextEncapsulator);
	}
}

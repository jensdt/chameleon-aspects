package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.RuntimeTransformationProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public abstract class AbstractAdviceTransformationProviderSupportingRuntime<T extends Element> extends AbstractAdviceTransformationProvider<T> implements RuntimeTransformationProvider<T> {

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	Subclasses should override transform instead of execute. Execute also starts the runtime transformations.
	 */
	@Override
	public final void execute(Advice advice, MatchResult joinpoint, WeavingEncapsulator previousEncapsulator, WeavingEncapsulator nextEncapsulator) throws LookupException {
		setAdvice(advice);
		setJoinpoint(joinpoint);
		T createdElement = transform(previousEncapsulator, nextEncapsulator);
		
		// Check if no element had to be created because it already existed.
		// If this is the case, don't perform runtime transformations
		if (createdElement == null)
			return;

		initialiseRuntimeTransformers(getJoinpoint());
		Coordinator<T> coordinator = getCoordinator(getJoinpoint(), previousEncapsulator, nextEncapsulator);
		if (coordinator != null)
			coordinator.transform(createdElement, getAdvice().formalParameters());
	}
	
	/**
	 * 	Transform the given advice
	 * 
	 * 	@param 	previous
	 * 			If there are multiple matches for a joinpoint, this parameter gives the previous one in the chain 
	 * 	@param 	next
	 * 			If there are multiple matches for a joinpoint, this parameter gives the next one in the chain 
	 * 	@throws LookupException
	 */
	public abstract T transform(WeavingEncapsulator previous, WeavingEncapsulator next)  throws LookupException;
}
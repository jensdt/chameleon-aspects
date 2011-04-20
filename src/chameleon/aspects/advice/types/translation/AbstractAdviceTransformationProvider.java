package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;


/**
 * 	Default implementation of some of the AdviceTransformationProvider methods
 *
 */
public abstract class AbstractAdviceTransformationProvider<T extends Element> implements AdviceTransformationProvider<T> {
	/**
	 * 	The joinpoint that is woven
	 */
	private MatchResult joinpoint;
	
	/**
	 * 	The advice that is transformed
	 */
	private Advice advice;
	
	/**
	 * 	The next AdviceTransformationProvider in the chain
	 */
	private AdviceTransformationProvider next;
	
	/**
	 * 	Constructor
	 * 
	 * 	@param 	joinpoint
	 * 			The joinpoint that is woven
	 * 	@param 	advice
	 * 			The advice that is transformed
	 */
	public AbstractAdviceTransformationProvider(MatchResult joinpoint, Advice advice) {
		this.joinpoint = joinpoint;
		this.advice = advice;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public Advice getAdvice() {
		return advice;
	}

	/**
	 * 	 {@inheritDoc}
	 */
	@Override
	public MatchResult getJoinpoint() {
		return joinpoint;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public AdviceTransformationProvider next() {
		return next;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void setNext(AdviceTransformationProvider next) {
		this.next = next;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void start(WeavingEncapsulator previousEncapsulator, WeavingEncapsulator nextEncapsulator) throws LookupException {
		T createdElement = transform(nextEncapsulator);

		if (next() != null)
			next.start(previousEncapsulator, nextEncapsulator);
	}
}

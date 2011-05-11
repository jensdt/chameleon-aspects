package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.exception.ChameleonProgrammerException;


/**
 * 	Default implementation of some of the AdviceTransformationProvider methods
 *
 */
public abstract class AbstractAdviceTransformationProvider<T extends Element> implements AdviceTransformationProvider {
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
	 * 	Get the advice to transform
	 * 
	 * 	@return The advice to transform
	 */
	public Advice getAdvice() {
		return advice;
	}

	/**
	 * 	Get the joinpoint this advice was applied to
	 * 
	 * 	@return	The joinpoint this advice was applied to
	 */
	public MatchResult getJoinpoint() {
		return joinpoint;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void execute(Advice advice, MatchResult joinpoint, WeavingEncapsulator previousEncapsulator, WeavingEncapsulator nextEncapsulator) throws LookupException {
		setAdvice(advice);
		setJoinpoint(joinpoint);
		transform(previousEncapsulator, nextEncapsulator);
	}
	
	/**
	 * 	Transform the given advice
	 * 
	 * 	NOTE: subclasses should directly override execute instead of this method. DO NOT OVERRIDE OR REIMPLEMENT THIS METHOD, IT IS OUTDATED.
	 * 
	 * @param	previous
	 * 			If there are multiple matches for a joinpoint, this parameter gives the previous one in the chain 
	 * @param 	next
	 * 			If there are multiple matches for a joinpoint, this parameter gives the next one in the chain 
	 * 
	 * 	@throws LookupException
	 */
	@Deprecated
	public T transform(WeavingEncapsulator previous, WeavingEncapsulator next)  throws LookupException {
		throw new ChameleonProgrammerException("Method transform not implemented. This method isn't abstract because it is deprecated, so a default implementation has been added. Override execute instead!!");
	}

	/**
	 * 	Set the advice to transform
	 * 
	 * 	@param 	advice
	 * 			The advice to transform
	 */
	protected void setAdvice(Advice advice) {
		this.advice = advice;
	}

	/**
	 * 	Set the joinpoint the advice is applied to
	 * 
	 * 	@param 	joinpoint
	 * 			The joinpoint the advice is applied to
	 */
	protected void setJoinpoint(MatchResult joinpoint) {
		this.joinpoint = joinpoint;
	}
}

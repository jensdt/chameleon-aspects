package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	Objects implementing this interface indicate they are responsible for transforming advice.
 * 	This are called, per advice, in a chain, after all the regular types have been woven.
 * 
 * 	Example: For method invocations, this transforms the advice as defined in the aspect to a static method.	
 * 
 * 	
 * 	@author Jens
 *
 */
public interface AdviceTransformationProvider<T extends Element> {
	/**
	 * 	Transform the given advice
	 * 
	 * 	@param 	next
	 * 			If there are multiple matches for a joinpoint, this parameter gives the next one in the chain 
	 * 	@throws LookupException
	 */
	public T transform(WeavingEncapsulator next)  throws LookupException;
	
	/**
	 * 	Get the next transformation provider in the chain
	 * 
	 * 	@return	The next transformation provider in the chain (possibly null)
	 */
	public AdviceTransformationProvider next();
	
	/**
	 * 	Set the next transformation provider in the chain to the given parameter
	 * 
	 * 	@param 	next
	 * 			The next transformation provider in the chain
	 */
	public void setNext(AdviceTransformationProvider next);

	/**
	 * 	Start the chain for advice transformations. This chain is always run completely - it doesn't end after one object transforms
	 * 
	 * 	@param 	advice
	 * 			The advice to transform
	 * 	@param 	previous
	 * 			If there are multiple matches for a joinpoint, this parameter gives the previous one in the chain 
	 * 	@param 	next
	 * 			If there are multiple matches for a joinpoint, this parameter gives the next one in the chain 
	 * 	@throws LookupException
	 */
	public void start(WeavingEncapsulator previous, WeavingEncapsulator next) throws LookupException;
	
	/**
	 * 	Get the joinpoint that matched
	 * 	
	 * 	@return The matched joinpoint
	 */
	public MatchResult getJoinpoint();
	
	/**
	 * 	Get the advice to be transformed
	 * 
	 * 	@return	The advice to be transformed
	 */
	public Advice getAdvice();
	
}
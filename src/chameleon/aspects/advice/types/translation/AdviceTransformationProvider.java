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
public interface AdviceTransformationProvider {

	/**
	 * 	Start the advice transformation
	 * 
	 * 	@param 	advice
	 * 			The advice to transform
	 *  @param	joinpoint
	 *  		The joinpoint the advice was applied to
	 * 	@param 	previous
	 * 			If there are multiple matches for a joinpoint, this parameter gives the previous one in the chain 
	 * 	@param 	next
	 * 			If there are multiple matches for a joinpoint, this parameter gives the next one in the chain 
	 * 	@throws LookupException
	 */
	public void execute(Advice advice, MatchResult joinpoint, WeavingEncapsulator previous, WeavingEncapsulator next) throws LookupException;	
}
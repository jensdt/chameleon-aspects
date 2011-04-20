package chameleon.aspects.weaver.weavingprovider;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.element.Element;

/**
 * 	Instances of WeavingProvider are responsible for determining how the join point and weaving result are combined to form the final result
 * 
 * 	@author Jens
 *
 * 	@param <T>	The type of the join point
 * 	@param <U>	The result type
 */
public interface WeavingProvider<T extends Element, U> {
	
	/**
	 * 	Execute the weaving
	 * 
	 * 	@param 	joinpoint
	 * 			The matched join point
	 * 	@param 	adviceResult
	 * 			The advice result
	 *  @param	advice
	 *  		The advice that is woven
	 * 	@param	previous
	 * 			If there are multiple matches for this join point, the previous one in the chain
	 * 	@param	next
	 * 			If there are multiple matches for this join point, the next one in the chain
	 */
	public void execute(MatchResult<? extends PointcutExpression, T> joinpoint, U adviceResult, Advice advice, WeavingEncapsulator previous, WeavingEncapsulator next);
}

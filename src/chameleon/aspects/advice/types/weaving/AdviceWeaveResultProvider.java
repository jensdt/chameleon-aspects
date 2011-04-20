package chameleon.aspects.advice.types.weaving;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	Objects implementing this interface are responsible for returning the result of weaving. They define how advice and join point
 * 	are combined to a result. (Note that these objects do *not* specify how the weaving itself should happen!) 
 * 
 * 	@author Jens
 *
 * 	@param <T>	The type of join point. This extends Element.
 * 	@param <U>	The type of the result. This can be anything (e.g. a MethodInvocation, a collection of statements, ...)
 */
public interface AdviceWeaveResultProvider<T extends Element, U> {
	/**
	 * 	Get the result of the weaving
	 * 
	 * 	@param 	advice
	 * 			The advice to weave
	 * 	@param 	joinpoint
	 * 			The given join point that was matched
	 * 	@return	The result of weaving
	 * 	@throws LookupException
	 */
	public U getWeaveResult(Advice advice, MatchResult<? extends PointcutExpression, ? extends T> joinpoint) throws LookupException;
}

package chameleon.aspects;

import java.util.AbstractSequentialList;
import java.util.Iterator;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.translation.AdviceTransformationProvider;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.weaver.weavingprovider.WeavingProvider;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	This class encapsulates everything needed to perform weaving:
 * 
 * 	- The weaving provider, the object that ties the joinpoint and weaving result together
 * 	- The weaing result provider, the object that returns the weaving result
 * 	- The advice transformation provider, the object that performs additional transformations on the advice
 * 
 *  - The joinpoint that was matched
 *  - The advice that has to be woven
 *  
 *  Furthermore, since multiple advices can be applied to the same joinpoint (with different providers), all encapsulators for a given joinpoint
 *  are organised in a double linked list. This is needed because sometimes, the result of the providers depends on the next or previous advice.
 *  
 *  This class ultimately starts the weaving process through its start method.
 *  
 * 	@author Jens De Temmerman
 *
 * 	@param <T>	The type of the joinpoint
 * 	@param <U>	The type of the result
 */
public class WeavingEncapsulator<T extends Element, U> {
	/**
	 * 	The weaving provider
	 */
	private WeavingProvider<T, U> weavingProvider;
	
	/**
	 * 	The weave result provider
	 */
	private AdviceWeaveResultProvider<T, U> weavingResultProvider;
	
	/**
	 * 	The transformation provider
	 */
	private AdviceTransformationProvider adviceTransformationProvider;
	
	/**
	 * 	The joinpoint
	 */
	private MatchResult<T> joinpoint;
	
	/**
	 * 	The advice
	 */
	private Advice<?> advice;
	
	/**
	 * 	The next encapsulator in the chain
	 */
	private WeavingEncapsulator<T, ?> next;
	
	/**
	 * 	The previous encapsulator in the chain
	 */
	private WeavingEncapsulator<T, ?> previous;
	
	/**
	 * 	Constructor
	 * 
	 * 	@param 	weavingProvider
	 * 			The weaving provider
	 * 	@param 	weavingResultProvider
	 * 			The weaving result provider
	 * 	@param 	adviceTransformationProvider
	 * 			The advice transformation provider
	 * 	@param 	advice
	 * 			The advice
	 * 	@param 	joinpoint
	 * 			The joinpoint
	 */
	public WeavingEncapsulator(WeavingProvider<T, U> weavingProvider, AdviceWeaveResultProvider<T, U> weavingResultProvider, AdviceTransformationProvider adviceTransformationProvider, Advice<?> advice, MatchResult<T> joinpoint) {
		this.weavingProvider = weavingProvider;
		this.weavingResultProvider = weavingResultProvider;
		this.adviceTransformationProvider = adviceTransformationProvider;
		this.advice = advice;
		this.joinpoint = joinpoint;
	}
	
	/**
	 * 	Start the weaving process
	 * 	
	 * 	@throws LookupException	FIXME: check this
	 */
	public void start() throws LookupException {
		// Get the weaving result
		U weavingResult = getWeavingResultProvider().getWeaveResult(getAdvice(), getJoinpoint());
		
		// Transform the advice
		getAdviceTransformationProvider().execute(getAdvice(), getJoinpoint(), previous, next);
		
		
		// Now call the weaving provider to combine the joinpoint and the weaving result
		getWeavingProvider().execute(getJoinpoint(), weavingResult, getAdvice(), previous, next);
		
		
		// Call the next weaver in the chain
		if (next != null)
			next.start();
	}
	
	/**
	 * 	Set the next weaving encapsulator in the chain
	 * 
	 * 	@param 	next
	 * 			The next weaving encapsulator
	 */
	private void setNext(WeavingEncapsulator<T, ?> next) {
		this.next = next;
	}
	
	/**
	 * 	Set the previous weaving encapsulator in the chain
	 * 
	 * @param 	previous
	 * 			The previous weaving encapsulator
	 */
	private void setPrevious(WeavingEncapsulator<T, ?> previous) {
		this.previous = previous;
	}
	
	/**
	 * 	Transform an iterable (e.g. a list) of weaving encapsulators to a double linked list
	 * 
	 * 	@param 	list
	 * 			The list of weaving encapsulators
	 * 	@return The head of the double linked list of weaving encapsulators
	 */
	public static WeavingEncapsulator fromIterable(Iterable<WeavingEncapsulator> list) {
		if (list == null)
			return null;
		
		Iterator<WeavingEncapsulator> iterator = list.iterator();
		
		if (!iterator.hasNext())
			return null;
		
		WeavingEncapsulator head = iterator.next();
		WeavingEncapsulator current = head;
		
		while (iterator.hasNext()) {
			WeavingEncapsulator next = iterator.next();
			current.setNext(next);
			next.setPrevious(current);
			
			current = next;
		}
		
		return head;
	}
	
	/**
	 * 	Get the weaving provider
	 * 
	 * 	@return	The weaving provider
	 */
	public WeavingProvider<T, U> getWeavingProvider() {
		return weavingProvider;
	}

	/**
	 * 	Get the weaving result provider
	 * 
	 * 	@return	The weaving result provider
	 */
	public AdviceWeaveResultProvider<T, U> getWeavingResultProvider() {
		return weavingResultProvider;
	}

	/**
	 * 	Get the advice transformation provider
	 * 
	 * 	@return	The advice transformation provider
	 */
	public AdviceTransformationProvider getAdviceTransformationProvider() {
		return adviceTransformationProvider;
	}
	
	/**
	 * 	Get the joinpoint
	 * 
	 * 	@return	The joinpoint
	 */
	public MatchResult<T> getJoinpoint() {
		return joinpoint;
	}

	/**
	 * 	Get the advice
	 * 
	 * 	@return	The advice
	 */
	public Advice<?> getAdvice() {
		return advice;
	}
}

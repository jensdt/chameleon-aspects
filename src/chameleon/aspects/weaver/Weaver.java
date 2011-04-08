package chameleon.aspects.weaver;

import java.util.List;

import org.rejuse.property.PropertySet;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.translation.AdviceTransformationProvider;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.weaver.weavingprovider.WeavingProvider;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	FIXME: change comments due to returning of WeavingEncapsulator<T, U> in some methods
 * 
 * 	This interface represents an element weaver. It has the responsibility to tie the different strategies together that allow weaving, as outlined below:
 * 
 * 		- 	First, it declares the <em>WeaveResultProvider</em>. This returns what the join point is transformed into by weaving.
 * 			This can differ by the advice type.
 * 			
 * 				Example 1: MethodInvocations
 * 				Here we have the same result for all different types. The join point (a method invocation) is simply transformed into a
 * 				different method invocation.
 * 
 * 		-	Second, it declares the <em>AdviceTransformationProvider</em>. This provides an opportunity to transform advice code, if this is necessary.
 *			Again, this can differ by the advice type.
 *
 *				Example 1: MethodInvocations
 *				The TranslationStrategy for method invocations is to create a new static method in a new class (denoting the aspect), if it doesn't exist yet.
 *				Note that, since the method is different for different advice types, this is differed by advice type.
 *		
 *		-	Third, it connects these two through a <em>WeavingProvider</em>. This outlines how the first two elements are connected.
 *
 *				Example 1: MethodInvocations
 *				Remember, the join point is a method invocation and so is the result of the weaver. We simply swap these to correctly implement
 *				the weaving.
 * 
 * 	All weavers are used together in a chain of responsibility.
 * 
 * @author Jens
 *
 * @param <T>	The type of the join point (so extends Element)
 * @param <U>	The type of the weaving result (can be anything, e.g. a MethodInvocation, or a List<Statement>, ...)
 */
public interface Weaver<T extends Element, U> {
	/**
	 * 	Get the object that calculates the result of weaving
	 * 
	 * 	@return	The object that is responsible for getting the weave-result 
	 */
	public AdviceWeaveResultProvider<T, U> getWeaveResultProvider();	
	
	/**
	 * 	Get the object that perform transformations to the advice. This is called per join point, since this may differ per join point
	 * 	For example, method invocations that handle exceptions can differ per join point in the exception clause - so each join point (more specifically, each distinct method matched by all join points)
	 * 	must have its own transformation.
	 * 
	 * 	@param 	advice
	 * 			The advice to transform
	 * 	@param 	joinpoint
	 * 			The join point this advice was matched on
	 * 	@return The object responsible for transformation
	 */
	public AdviceTransformationProvider<T> getTransformationStrategy(Advice advice, MatchResult<? extends PointcutExpression, ? extends Element> joinpoint);
	
	/**
	 * 	Get the object responsible for tying the weave result and original join point together
	 * 
	 * 	@param  Advice
	 * 			The advice to weave
	 * 
	 * 	@return	The object responsible
	 */
	public WeavingProvider<T, U> getWeavingProvider(Advice advice);
	
	/**
	 * 	Check if this weaver supports the given join point and advice
	 * 
	 * 	@param 	joinpoint
	 * 			The join point to check
	 * 	@param 	advce
	 * 			the advice
	 * 	@return	True if this weaver supports the join point and advice type, false otherwise
	 */
	public boolean supports(Advice advice, MatchResult<? extends PointcutExpression, T> result) throws LookupException;
	
	/**
	 * 	Get a list of all supported types by this weaver
	 * 	
	 * 	@return	the list of supported types
	 */
	public List<Class<T>> supportedTypes();
	
	/**
	 * 	Perform the actual weaving
	 * 
	 * 	@param 	advice
	 * 			The advice to weave
	 * 	@param 	joinpoints
	 * 			The join points belonging to that advice
	 * 	@throws LookupException
	 */
	public WeavingEncapsulator<T, U> weave(Advice advice, MatchResult<? extends PointcutExpression, T> joinpoints) throws LookupException;
	
	/**
	 * 	Get the list of supported property sets by this weaver
	 * 
	 * 	@param 	advice
	 * 			The advice to weave
	 * 	@returns The list of supported advice property sets
	 */
	public List<PropertySet> supportedAdviceProperties(Advice advice);
	
	/**
	 * 	The start of the weaving process - each weaver is called until one can handle it. No further weavers are called once handled
	 * 
	 * 	@param 	compilationUnit
	 * 			The	compilation unit to weave 
	 * 	@param 	advice
	 * 			The advice to weave
	 * 	@param 	joinpoint
	 * 			The join points belonging to that advice
	 * 	@throws LookupException
	 */
	public WeavingEncapsulator<T, U> start(Advice<?> advice, MatchResult<? extends PointcutExpression, T> joinpoint) throws LookupException;
	
	/**
	 * 	Handle the given compilation unit, advice and join point. Used for the Chain Of Responsibility - return true if this weaver can weave
	 * 	the given parameters and perform the actual weaving, return false if it can't.
	 * 	
	 * 	@param 	compilationUnit
	 * 			The compilation unit to weave
	 * 	@param 	advice
	 * 			The advice to weave
	 * 	@param 	joinpoint
	 * 			The join points belonging to that advice
	 * @return	True if this weaver can weave the given parameters, false if not
	 * 
	 * @throws 	LookupException
	 */
	public WeavingEncapsulator<T, U> handle(Advice advice, MatchResult<? extends PointcutExpression, T> joinpoint) throws LookupException;

	/**
	 * 	Get the next weaver in the chain
	 * 
	 * 	@return	The next weaver in the chain (possibly null)
	 */
	public Weaver next();
	
	/**
	 * 	Set the next weaver in the chain to the given parameter
	 * 
	 * 	@param 	next
	 * 			The next weaver in the chain
	 */
	public void setNext(Weaver next);
}

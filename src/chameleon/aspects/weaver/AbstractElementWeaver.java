package chameleon.aspects.weaver;

import java.util.Iterator;
import java.util.List;

import org.rejuse.property.PropertySet;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 *	For general info about how the elementWeavers work, see Weaver.java 
 * 
 * 	@author Jens
 *
 */
public abstract class AbstractElementWeaver<T extends Element, U> implements Weaver<T, U> {
		
	/**
	 * 	The next weaver in the chain
	 */
	private Weaver next;
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void setNext(Weaver next) {
		this.next = next;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public Weaver next() {
		return next;
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public final WeavingEncapsulator<T, U> start(Advice<?> advice, MatchResult<? extends PointcutExpression, T> joinpoint) throws LookupException {
		WeavingEncapsulator<T, U> isHandled = handle(advice, joinpoint);
		
		if (isHandled != null)
			return isHandled;
		
		if (next() == null)
			throw new RuntimeException("No matching weaver found in chain for joinpoint of type " + joinpoint.getJoinpoint().getClass());
		
		return next().start(advice, joinpoint);
		
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public WeavingEncapsulator<T, U> handle(Advice advice, MatchResult<? extends PointcutExpression, T> joinpoint) throws LookupException {
		if (!supports(advice, joinpoint))
			return null;
		
		return weave(advice, joinpoint);
	}
	
	/**
	 * 	{@inheritDoc}
	 * 
	 * 	True if the type of the join point is a sub type of a supported type, false otherwise
	 */
	@Override
	public boolean supports(Advice advice, MatchResult<? extends PointcutExpression, T> result) throws LookupException {
		boolean supports = false;
		
		// Get all supported property sets for the advice
		List<PropertySet> supportedProperties = supportedAdviceProperties(advice);
		// Now, for each property set, ALL properties of the given advice must be in the supported set
		Iterator<PropertySet> propertySetIterator = supportedProperties.iterator();
		while (!supports && propertySetIterator.hasNext()) {
			supports = propertySetIterator.next().containsAll(advice.properties().properties());
		}
		
		if (!supports)
			return false;
			
		Class<? extends Element> c = result.getJoinpoint().getClass();
		
		for (Class<T> supported : supportedTypes()) {
			if (supported.isAssignableFrom(c))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public WeavingEncapsulator<T, U> weave(Advice advice, MatchResult<? extends PointcutExpression, T> matchResult) throws LookupException {
		WeavingEncapsulator<T, U> encapsulator = new WeavingEncapsulator<T, U>(getWeavingProvider(advice), getWeaveResultProvider(), getTransformationStrategy(advice, matchResult), advice, matchResult);

		return encapsulator;
	}
	
	protected PropertySet getAround(Advice advice) {
		return new PropertySet().with(advice.language().property("advicetype.around"));		
	}
	
	protected PropertySet getBefore(Advice advice) {
		return new PropertySet().with(advice.language().property("advicetype.before"));
	}
	
	protected PropertySet getAfter(Advice advice) {
		return new PropertySet().with(advice.language().property("advicetype.after"));
	}
	
	protected PropertySet getAfterReturning(Advice advice) {
		PropertySet afterReturning = new PropertySet();
		afterReturning.add(advice.language().property("advicetype.after"));
		afterReturning.add(advice.language().property("advicetype.returning"));
	
		return afterReturning;
	}
	
	protected PropertySet getAfterThrowing(Advice advice) {
		PropertySet afterThrowing = new PropertySet();
		afterThrowing.add(advice.language().property("advicetype.after"));
		afterThrowing.add(advice.language().property("advicetype.throwing"));
		
		return afterThrowing;
	}
}

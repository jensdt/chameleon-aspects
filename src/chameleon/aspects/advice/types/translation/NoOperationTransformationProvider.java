package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	Represents a transformation that doesn't do anything.
 * 
 * 	@author Jens
 *
 */
public class NoOperationTransformationProvider extends AbstractAdviceTransformationProvider<Element> {

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	No operation, so don't do anything
	 */
	@Override
	public void execute(Advice advice, MatchResult joinpoint, WeavingEncapsulator previous, WeavingEncapsulator next) throws LookupException {

	}
}
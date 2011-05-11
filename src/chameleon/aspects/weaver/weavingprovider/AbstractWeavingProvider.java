package chameleon.aspects.weaver.weavingprovider;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;

public abstract class AbstractWeavingProvider<T extends Element, U> implements WeavingProvider<T, U> {
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void execute(MatchResult<T> joinpoint, U adviceResult, Advice advice, WeavingEncapsulator previous, WeavingEncapsulator next) {
		executeWeaving(joinpoint, adviceResult);
	}
	
	/**
	 * 	Execute the actual weaving
	 * 
	 * 	@param 	joinpoint
	 * 			The joinpoint to weave at
	 * 	@param 	adviceResult
	 * 			The code to weave in
	 */
	protected abstract void executeWeaving(MatchResult<T> joinpoint, U adviceResult);
}

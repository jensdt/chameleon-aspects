package chameleon.aspects.advice.types.translation;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.AbstractCoordinator;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;


/**
 * 	Default implementation of some of the AdviceTransformationProvider methods
 *
 */
public abstract class AbstractAdviceTransformationProvider<T extends Element> implements AdviceTransformationProvider<T> {
	private MatchResult joinpoint;
	
	
	/**
	 * 	The next AdviceTransformationProvider in the chain
	 */
	private AdviceTransformationProvider next;
	
	public AbstractAdviceTransformationProvider(MatchResult joinpoint) {
		this.joinpoint = joinpoint;
	}

	@Override
	public MatchResult getJoinpoint() {
		return joinpoint;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public AdviceTransformationProvider next() {
		return next;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void setNext(AdviceTransformationProvider next) {
		this.next = next;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public final void start(Advice<?> advice) throws LookupException {
		T createdElement = transform(advice);
		
		//FIXME: place this somewhere else
		Coordinator<T> coordinator = getCoordinator();
		coordinator.transform(createdElement);
//		
//		List<? extends RuntimePointcutExpression> runtimePces = getJoinpoint().getExpression().getAllRuntimePointcutExpressions();
//		for (RuntimePointcutExpression expr : runtimePces)
//			if (canTransform(expr))
//				getRuntimeTransformer(expr).transform(createdElement, expr);
//		
		if (next() != null)
			next.start(advice);
	}

	protected abstract Coordinator<T> getCoordinator();
}

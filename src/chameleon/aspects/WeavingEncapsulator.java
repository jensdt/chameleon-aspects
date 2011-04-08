package chameleon.aspects;

import java.util.Iterator;
import java.util.List;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.translation.AdviceTransformationProvider;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.weaver.weavingprovider.WeavingProvider;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class WeavingEncapsulator<T extends Element, U> {
	private WeavingProvider<T, U> weavingProvider;
	private AdviceWeaveResultProvider<T, U> weavingResultProvider;
	private AdviceTransformationProvider<T> adviceTransformationProvider;
	private MatchResult<? extends PointcutExpression, T> joinpoint;
	private Advice<?> advice;
	
	private WeavingEncapsulator next;
	private WeavingEncapsulator previous;
	
	public WeavingEncapsulator(WeavingProvider<T, U> weavingProvider, AdviceWeaveResultProvider<T, U> weavingResultProvider, AdviceTransformationProvider<T> adviceTransformationProvider, Advice<?> advice, MatchResult<? extends PointcutExpression, T> joinpoint) {
		this.weavingProvider = weavingProvider;
		this.weavingResultProvider = weavingResultProvider;
		this.adviceTransformationProvider = adviceTransformationProvider;
		this.advice = advice;
		this.joinpoint = joinpoint;
	}
	
	private void setNext(WeavingEncapsulator next) {
		this.next = next;
	}
	
	private void setPrevious(WeavingEncapsulator previous) {
		this.previous = previous;
	}
	
	public static WeavingEncapsulator fromList(List<WeavingEncapsulator> list) {
		if (list == null || list.isEmpty())
			return null;
		
		Iterator<WeavingEncapsulator> iterator = list.iterator();
		
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
	
	public WeavingProvider<T, U> getWeavingProvider() {
		return weavingProvider;
	}

	public AdviceWeaveResultProvider<T, U> getWeavingResultProvider() {
		return weavingResultProvider;
	}

	public AdviceTransformationProvider<T> getAdviceTransformationProvider() {
		return adviceTransformationProvider;
	}
	
	public MatchResult<? extends PointcutExpression, T> getJoinpoint() {
		return joinpoint;
	}

	public void setJoinpoint(MatchResult<? extends PointcutExpression, T> joinpoint) {
		this.joinpoint = joinpoint;
	}

	public Advice<?> getAdvice() {
		return advice;
	}

	public void start() throws LookupException {
		getWeavingProvider().execute(getJoinpoint(), getWeavingResultProvider().getWeaveResult(getAdvice(), (MatchResult) getJoinpoint()), getAdvice(), previous, next);
		getAdviceTransformationProvider().start(previous, next);
		
		if (next != null)
			next.start();
	}
}

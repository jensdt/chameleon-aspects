package chameleon.aspects.advice.types.weaving;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class ReturnAdviceProvider<T extends Element> implements AdviceWeaveResultProvider<T, Element> {

	@Override
	public Element getWeaveResult(Advice advice, MatchResult<T> joinpoint) throws LookupException {
		return advice.body();
	}
}
package chameleon.aspects.advice.types.weaving;

import java.util.List;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Statement;

public class ReturnAdviceProvider<T extends Element> implements AdviceWeaveResultProvider<T, List<Statement>> {

	@Override
	public List<Statement> getWeaveResult(Advice advice, MatchResult<T> joinpoint) throws LookupException {
		return advice.body().statements();
	}
}
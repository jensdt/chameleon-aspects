package chameleon.aspects.pointcut.expression;

import javax.management.RuntimeErrorException;

import chameleon.aspects.pointcut.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionNot<E extends PointcutExpressionNot<E>> extends PointcutExpressionSingle<E> {

	public PointcutExpressionNot(PointcutExpression expression) {
		super(expression);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		throw new RuntimeException();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionNot<E>(expression().clone());
	}

}

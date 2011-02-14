package chameleon.aspects.pointcut.expression;

import chameleon.aspects.pointcut.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionOr<E extends PointcutExpressionOr<E>> extends PointcutExpressionDual<E> {

	public PointcutExpressionOr(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		throw new RuntimeException();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionOr<E>(expression1().clone(), expression2().clone());
	}

}

package chameleon.aspects.pointcut.expression;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionNot<E extends PointcutExpressionNot<E>> extends PointcutExpressionSingle<E> {

	public PointcutExpressionNot(PointcutExpression expression) {
		super(expression);
	}

	@Override
	public boolean matches(Element joinpoint) throws LookupException {
		return expression() == null || ! expression().matches(joinpoint);
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionNot<E>(expression().clone());
	}

}

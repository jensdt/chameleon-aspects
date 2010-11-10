package chameleon.aspects.pointcutexpression;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionOr<E extends PointcutExpressionOr<E>> extends PointcutExpressionDual<E> {

	public PointcutExpressionOr(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public boolean matches(Element joinpoint) throws LookupException {
		return (expression1() == null || expression1().matches(joinpoint))
			|| (expression2() == null || expression2().matches(joinpoint));
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionOr<E>(expression1().clone(), expression2().clone());
	}

}

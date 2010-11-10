package chameleon.aspects.pointcutexpression;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionAnd<E extends PointcutExpressionAnd<E>> extends PointcutExpressionDual<E> {

	public PointcutExpressionAnd(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public boolean matches(Element joinpoint) throws LookupException {
		return (expression1() == null || expression1().matches(joinpoint))
			&& (expression2() == null || expression2().matches(joinpoint));
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionAnd(expression1().clone(), expression2().clone());
	}
}

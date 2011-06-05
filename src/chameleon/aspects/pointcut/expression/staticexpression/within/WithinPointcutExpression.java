package chameleon.aspects.pointcut.expression.staticexpression.within;

import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;

public abstract class WithinPointcutExpression<E extends WithinPointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {
	@Override
	public boolean isSupported(Class<? extends Element> c) {
		return true;
	}	
}
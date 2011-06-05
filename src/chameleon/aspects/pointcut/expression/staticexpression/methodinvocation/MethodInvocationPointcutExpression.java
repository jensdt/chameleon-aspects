package chameleon.aspects.pointcut.expression.staticexpression.methodinvocation;

import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.MethodInvocation;

public abstract class MethodInvocationPointcutExpression<E extends MethodInvocationPointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {

	@Override
	public boolean isSupported(Class<? extends Element> c) {
		if (MethodInvocation.class.isAssignableFrom(c))
			return true;
		
		return false;
	}
}

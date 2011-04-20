package chameleon.aspects.pointcut.expression.staticexpression.methodinvocation;

import java.util.Collections;
import java.util.Set;

import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.MethodInvocation;

public abstract class MethodInvocationPointcutExpression<E extends MethodInvocationPointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {
	
	@Override
	public Set<Class<? extends Element>> supportedJoinpoints() {
		return Collections.<Class<? extends Element>>singleton(MethodInvocation.class);
	}
}

package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.HashSet;
import java.util.Set;

import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.MethodInvocation;

public abstract class MethodInvocationPointcutExpression<E extends MethodInvocationPointcutExpression<E, T>, T extends MethodInvocation> extends PointcutExpression<E, T> {
	
	@Override
	public Set<Class> supportedJoinpoints() {
		Set<Class> result = new HashSet<Class>();
		result.add(MethodInvocation.class);
		return result;
	}
}

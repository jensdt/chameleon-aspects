package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.HashSet;
import java.util.Set;

import chameleon.aspects.pointcut.expression.generic.StaticPointcutExpression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.variable.FormalParameter;

public abstract class MethodInvocationPointcutExpression<E extends MethodInvocationPointcutExpression<E>> extends StaticPointcutExpression<E> {
	
	@Override
	public Set<Class> supportedJoinpoints() {
		Set<Class> result = new HashSet<Class>();
		result.add(MethodInvocation.class);
		return result;
	}
}

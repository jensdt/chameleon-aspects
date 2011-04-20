package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.expression.Expression;

/**
 * 	Represents an expression provider for runtime checks. Given a runtime check, return the matching expression.

 * 	@author Jens
 *
 */
public interface RuntimeExpressionProvider<T extends RuntimePointcutExpression<?>> {
	public Expression<?> getExpression(T expr, NamingRegistry<RuntimePointcutExpression<?>> namingRegistry);
}

package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.expression.Expression;

/**
 * 	Represents an expression provider for runtime checks. Given a runtime check, return the matching expression.

 * 	@author Jens
 *
 */
public interface RuntimeExpressionProvider {
	public Expression<?> getExpression(RuntimePointcutExpression<?> expr);
}

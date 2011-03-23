package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.IfPointcutExpression;
import chameleon.core.expression.Expression;

/**
 * 	Performs a runtime check with a given boolean expression
 * 
 * 	@author Jens
 *
 */
public class RuntimeIfCheck implements RuntimeExpressionProvider {
	
	/**
	 * 	{@inheritDoc}
	 * 		
	 *	This is trivial, since the expression is exactly that one contained in the if expression
	 */
	@Override
	public Expression<?> getExpression(RuntimePointcutExpression<?> expr) {
		if (!(expr instanceof IfPointcutExpression))
			return null;
		
		IfPointcutExpression<?> ifCheck = (IfPointcutExpression<?>) expr;
		
		return ifCheck.expression().clone();
	}
}

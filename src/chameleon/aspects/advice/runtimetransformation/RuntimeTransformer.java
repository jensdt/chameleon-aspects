package chameleon.aspects.advice.runtimetransformation;

import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.expression.Expression;


public abstract class RuntimeTransformer<T> {
	public abstract void transform(T element, RuntimePointcutExpression expr);
	public abstract Expression getExpression(RuntimePointcutExpression expr);
}

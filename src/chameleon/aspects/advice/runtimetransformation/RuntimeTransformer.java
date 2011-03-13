package chameleon.aspects.advice.runtimetransformation;

import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;


public abstract class RuntimeTransformer<T> {
	public abstract void transform(T element, RuntimePointcutExpression expr);
}

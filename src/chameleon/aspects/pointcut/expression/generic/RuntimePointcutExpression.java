package chameleon.aspects.pointcut.expression.generic;

import chameleon.aspects.pointcut.expression.PointcutExpression;


public interface RuntimePointcutExpression<E extends RuntimePointcutExpression<E>> extends PointcutExpression<E> {

}

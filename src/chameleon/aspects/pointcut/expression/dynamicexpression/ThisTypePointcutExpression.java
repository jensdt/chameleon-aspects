package chameleon.aspects.pointcut.expression.dynamicexpression;

import java.util.List;

import chameleon.core.expression.NamedTargetExpression;

public class ThisTypePointcutExpression<E extends ThisTypePointcutExpression<E>> extends TypePointcutExpression<E> {
	
	public ThisTypePointcutExpression(NamedTargetExpression parameter) {
		super(parameter);
	}

	@Override
	public E clone() {
		ThisTypePointcutExpression<E> clone = new ThisTypePointcutExpression<E>(parameter().clone());		
		return (E) clone;
	}
}

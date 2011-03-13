package chameleon.aspects.pointcut.expression.runtime;

import chameleon.core.expression.NamedTargetExpression;

public class TargetTypePointcutExpression<E extends TargetTypePointcutExpression<E>> extends TypePointcutExpression<E> {
	
	public TargetTypePointcutExpression(NamedTargetExpression parameter) {
		super(parameter);
	}

	@Override
	public E clone() {
		TargetTypePointcutExpression<E> clone = new TargetTypePointcutExpression<E>(parameter().clone());		
		return (E) clone;
	}
}

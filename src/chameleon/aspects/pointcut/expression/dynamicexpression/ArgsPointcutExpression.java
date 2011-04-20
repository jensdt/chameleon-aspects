package chameleon.aspects.pointcut.expression.dynamicexpression;

import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;

public class ArgsPointcutExpression<E extends ArgsPointcutExpression<E>> extends AbstractParameterExposurePointcutExpression<E> implements RuntimePointcutExpression<E>, ParameterExposurePointcutExpression<E> {
	
	private OrderedMultiAssociation<ArgsPointcutExpression<E>, NamedTargetExpression> _parameters = new OrderedMultiAssociation<ArgsPointcutExpression<E>, NamedTargetExpression>(this);
	
	public List<NamedTargetExpression> parameters() {
		return _parameters.getOtherEnds();
	}
	
	public void add(NamedTargetExpression parameter) {
		setAsParent(_parameters, parameter);
	}
	
	public void addAll(List<NamedTargetExpression> parameters) {
		for (NamedTargetExpression t : parameters)
			add(t);
	}

	@Override
	public List<? extends Element> children() {
		return parameters();
	}

	@Override
	public E clone() {
		ArgsPointcutExpression<E> clone = new ArgsPointcutExpression<E>();
		
		for (NamedTargetExpression type : parameters())
			clone.add(type.clone());
		
		return (E) clone;
	}	
}
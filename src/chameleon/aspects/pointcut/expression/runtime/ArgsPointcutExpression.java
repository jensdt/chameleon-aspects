package chameleon.aspects.pointcut.expression.runtime;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class ArgsPointcutExpression<E extends ArgsPointcutExpression<E>> extends RuntimePointcutExpression<E> implements ParameterExposurePointcutExpression {
	
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
	public Set<Class> supportedJoinpoints() {
		Set<Class> supported = new HashSet<Class>();

		supported.add(Element.class);
		return supported;
	}

	@Override
	public E clone() {
		ArgsPointcutExpression clone = new ArgsPointcutExpression();
		
		for (NamedTargetExpression type : parameters())
			clone.add(type.clone());
		
		return (E) clone;
	}

	@Override
	public boolean hasParameter(FormalParameter fp) {
		return indexOfParameter(fp) != -1;
	}

	@Override
	public int indexOfParameter(FormalParameter fp) {
		if (parameters() == null)
			return -1;	
		
		for (int i = 0; i < parameters().size(); i++) {
			try {
				// FIXME: this is temporary - when a named pointcut is a pointcut expression, this should revert
//				if (parameters().get(i).getElement() == fp)
//					return i;
				if (parameters().get(i).getElement() instanceof FormalParameter) {
					FormalParameter param = (FormalParameter) parameters().get(i).getElement();
					
					if (param.signature().name().equals(fp.signature().name()) && param.getType().sameAs(fp.getType()))
						return i;
				}
			} catch (LookupException e) {
				// Ignore
			}
		}
		
		return -1;
	}
	
	public void renameParameters(List<String> newParameterNames) {
		for (int i = 0; i < parameters().size(); i++) {
			NamedTargetExpression parameter = parameters().get(i);
			try {
				if (parameter.getElement() instanceof FormalParameter) {
					parameter.getElement().setName(newParameterNames.get(i));
				}
			} catch (LookupException e) {
				// ignore
			}
		}
	}
}
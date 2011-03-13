package chameleon.aspects.pointcut.expression.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class ArgsPointcutExpression<E extends ArgsPointcutExpression<E>> extends RuntimePointcutExpression<E> {
	
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
	public MatchResult matches(Element joinpoint) throws LookupException {
		return new MatchResult(this, joinpoint);
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
				if (parameters().get(i).getElement() == fp)
					return i;
			} catch (LookupException e) {
				// Ignore
			}
		}
		
		return -1;
	}

}

package chameleon.aspects.pointcut.expression.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public abstract class TypePointcutExpression<E extends TypePointcutExpression<E>> extends RuntimePointcutExpression<E> {
	public TypePointcutExpression(NamedTargetExpression parameter) {
		setParameter(parameter);
	}

	private SingleAssociation<TypePointcutExpression<E>, NamedTargetExpression> _parameter = new SingleAssociation<TypePointcutExpression<E>, NamedTargetExpression>(this);
	
	public NamedTargetExpression parameter() {
		return _parameter.getOtherEnd();
	}
	
	public void setParameter(NamedTargetExpression parameter) {
		setAsParent(_parameter, parameter);
	}
	
	public TypeReference getType() {
		try {
			return new BasicTypeReference(parameter().getType().getFullyQualifiedName());
		} catch (LookupException e) {
			
		}
		
		return null;
	}
	
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return false;
	}

	@Override
	public int indexOfParameter(FormalParameter fp) {
		return -1;
	}

	@Override
	public Set<Class> supportedJoinpoints() {
		Set<Class> supported = new HashSet<Class>();

		supported.add(Element.class);
		return supported;
	}
	
	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		return new MatchResult(this, joinpoint);
	}
	
	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(parameter());
	}
}

package chameleon.aspects.pointcut.expression.runtime;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class TypeOrParameter<E extends TypeOrParameter<E>> extends NamespaceElementImpl<E> {
	
	private SingleAssociation<TypeOrParameter<E>, TypeReference> _type = new SingleAssociation<TypeOrParameter<E>, TypeReference>(this);
	private SingleAssociation<TypeOrParameter<E>, Expression> _parameter = new SingleAssociation<TypeOrParameter<E>, Expression>(this);
	
	public TypeOrParameter() {
		
	}
	
	public TypeOrParameter(TypeReference type) {
		setType(type);
	}
	
	public TypeOrParameter(Expression param) {
		setParameter(param);
	}
	
	public TypeReference type() {
		return _type.getOtherEnd();
	}
	
	public Expression parameter()  {
		return _parameter.getOtherEnd();
	}
	
	public void setType(TypeReference type) {
		setAsParent(_parameter, null);
		setAsParent(_type, type);
	}
	
	public void setParameter(Expression param) {
		setAsParent(_type, null);
		setAsParent(_parameter, param);
	}
	
	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(type(), result);
		Util.addNonNull(parameter(), result);
		
		return result;
	}

	@Override
	public E clone() {
		TypeOrParameter clone = new TypeOrParameter();
		
		if (type() != null)
			clone.setType(type().clone());
		
		if (parameter() != null)
			clone.setParameter(parameter().clone());
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	public TypeReference getType() {
		if (type() != null)
			return type();
		else
			try {
				return new BasicTypeReference(parameter().getType().getFullyQualifiedName());
			} catch (LookupException e) {
				return null;
			}
	}

}

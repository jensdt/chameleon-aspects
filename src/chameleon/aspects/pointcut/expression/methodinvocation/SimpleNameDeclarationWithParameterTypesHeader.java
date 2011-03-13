package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.List;

import org.rejuse.association.MultiAssociation;

import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.TypeReference;

public class SimpleNameDeclarationWithParameterTypesHeader<E extends SimpleNameDeclarationWithParameterTypesHeader<E>> extends NamespaceElementImpl<E> {

	private String name;
	private MultiAssociation<SimpleNameDeclarationWithParameterTypesHeader<E>, TypeReference> _types = new MultiAssociation<SimpleNameDeclarationWithParameterTypesHeader<E>, TypeReference>(this);
	
	public SimpleNameDeclarationWithParameterTypesHeader(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
	public List<TypeReference> types() {
		return _types.getOtherEnds();
	}
	
	public void add(TypeReference type) {
		setAsParent(_types, type);
	}
	
	public void addAll(List<TypeReference> types) {
		if (types == null)
			return;
		
		for (TypeReference type : types)
			add(type);
	}
	
	
	@Override
	public List<? extends Element> children() {
		return types();
	}

	@Override
	public E clone() {
		SimpleNameDeclarationWithParameterTypesHeader clone = new SimpleNameDeclarationWithParameterTypesHeader<E>(name);
		
		for (TypeReference type : types())
			clone.add(type.clone());
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
}
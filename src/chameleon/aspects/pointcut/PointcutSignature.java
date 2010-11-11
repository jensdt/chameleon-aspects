package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;

import chameleon.core.declaration.DeclarationWithParametersSignature;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class PointcutSignature<E extends PointcutSignature<E>> extends
		DeclarationWithParametersSignature<E, Element> {

	private String _name;
	private OrderedMultiAssociation<PointcutSignature, TypeReference> _parameterTypes = new OrderedMultiAssociation<PointcutSignature, TypeReference>(
			this);

	public PointcutSignature(String name) {
		setName(name);
	}

	@Override
	public List<? extends Element> children() {
		return typeReferences();
	}

	@Override
	public E clone() {
		PointcutSignature<E> clone = new PointcutSignature<E>(name());
		
		for (TypeReference ref : typeReferences())
			clone.add(ref.clone());
		
		return (E) clone;
	}

	@Override
	public String name() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public List<Signature> signatures() {
		return Util.createSingletonList((Signature) this);
	}

	@Override
	public Signature lastSignature() {
		return this;
	}

	@Override
	public int length() {
		return 1;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		if (name() == null) {
			result = result.and(new BasicProblem(this,
					"The signature has no name."));
		}
		return result;
	}

	public void add(TypeReference arg) {
		_parameterTypes.add(arg.parentLink());
	}

	@Override
	public int nbFormalParameters() {
		return _parameterTypes.size();
	}

	public List<TypeReference> typeReferences() {
		return _parameterTypes.getOtherEnds();
	}

	@Override
	public List<Type> parameterTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();

		for (TypeReference ref : typeReferences())
			result.add(ref.getType());

		return result;
	}

}

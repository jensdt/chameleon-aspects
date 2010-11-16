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
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class PointcutSignature<E extends PointcutSignature<E>> extends
		DeclarationWithParametersSignature<E, Element> {

	private String _name;
	private OrderedMultiAssociation<PointcutSignature, FormalParameter> _parameters = new OrderedMultiAssociation<PointcutSignature, FormalParameter>(
			this);

	public PointcutSignature(String name) {
		setName(name);
	}

	@Override
	public List<? extends Element> children() {
		return formalParemeters();
	}

	@Override
	public E clone() {
		PointcutSignature<E> clone = new PointcutSignature<E>(name());
		
		for (FormalParameter ref : formalParemeters())
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

	public void add(FormalParameter arg) {
		setAsParent(_parameters, arg);
	}

	@Override
	public int nbFormalParameters() {
		return _parameters.size();
	}

	public List<FormalParameter> formalParemeters() {
		return _parameters.getOtherEnds();
	}

	@Override
	public List<Type> parameterTypes() throws LookupException {
		List<Type> result = new ArrayList<Type>();

		for (FormalParameter ref : formalParemeters())
			result.add(ref.getType());

		return result;
	}

}

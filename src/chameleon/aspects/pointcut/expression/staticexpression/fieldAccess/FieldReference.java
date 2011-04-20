package chameleon.aspects.pointcut.expression.staticexpression.fieldAccess;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.element.ElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class FieldReference<E extends FieldReference<E>> extends ElementImpl<E> {

	private String reference;
	
	public FieldReference(String reference) {
		this.reference = reference;
	}
	
	public String reference() {
		return reference;
	}
	
	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public E clone() {
		return (E) new FieldReference(reference);
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}

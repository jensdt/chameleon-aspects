package chameleon.aspects.pointcut;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class AnnotationReference<E extends AnnotationReference<E>> extends NamespaceElementImpl<E> {
	
	private String reference;
	
	public AnnotationReference(String reference) {
		setReference(reference);
	}
	
	public String reference() {
		return reference;
	}

	private void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public E clone() {
		return (E) new AnnotationReference(reference());
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}

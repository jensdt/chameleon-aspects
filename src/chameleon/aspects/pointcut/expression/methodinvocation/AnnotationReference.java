package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public class AnnotationReference<E extends AnnotationReference<E>> extends NamespaceElementImpl<E> {
	
	private String referencedName;
	
	public AnnotationReference(String reference) {
		setReferencendName(reference);
	}
	
	public String referencendName() {
		return referencedName;
	}

	private void setReferencendName(String reference) {
		this.referencedName = reference;
	}

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public E clone() {
		return (E) new AnnotationReference(referencendName());
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

}

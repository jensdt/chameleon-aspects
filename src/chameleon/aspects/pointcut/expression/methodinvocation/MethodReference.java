package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

/**
 * 	Represents a reference to a method, used in a pointcut description. References to methods are always fully qualified and
 * 	contain both the return type of the method and the formal parameters.
 *
 * 	@author jensdt
 *
 */
public class MethodReference<E extends MethodReference<E>> extends NamespaceElementImpl<E> {
	
	public MethodReference(QualifiedMethodHeader fqn, TypeReference type, String typeNameWithWC) {
		setTypeNameWithWC(typeNameWithWC);
		setType(type);
		setFqn(fqn);
	}
	
	public boolean hasExplicitType() {
		return type() != null;
	}
	
	/**
	 *	Return type of the method 	
	 */
	private String typeNameWithWC;
	
	private void setTypeNameWithWC(String typeNameWithWC) {
		this.typeNameWithWC = typeNameWithWC;
	}
	
	public String typeNameWithWC() {
		return typeNameWithWC;
	}
	
	public String getFullyQualifiedName() {
		return fqn().getFullyQualifiedName();
	}

	/**
	 * 	The fully qualified name of the method
	 */
	private SingleAssociation<MethodReference<E>, QualifiedMethodHeader> _fqn = new SingleAssociation<MethodReference<E>, QualifiedMethodHeader>(this);
		
	private void setFqn(QualifiedMethodHeader fqn) {
		setAsParent(_fqn, fqn);
	}

	public QualifiedMethodHeader fqn() {
		return _fqn.getOtherEnd();
	}

	private SingleAssociation<MethodReference<E>, TypeReference> _typeRef = new SingleAssociation<MethodReference<E>, TypeReference>(this);

	private void setType(TypeReference type) {
		setAsParent(_typeRef, type);
	}
	
	public TypeReference type() {
		return _typeRef.getOtherEnd();
	}


	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		Util.addNonNull(fqn(), result);
		Util.addNonNull(type(), result);
		return result;
	}

	@Override
	public E clone() {
		TypeReference typeClone = null;
		if (type() != null)
			typeClone = type().clone();
		
		return (E) new MethodReference<E>((QualifiedMethodHeader) fqn().clone(), typeClone, typeNameWithWC);
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		
		if (type() == null)
			result.and(new BasicProblem(this, "The type of the method reference may not be null"));
		
		if (fqn() == null)
			result.and(new BasicProblem(this, "The fully qualified name of the method reference may not be null"));
		
		return result;
	}
}

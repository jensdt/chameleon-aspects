package chameleon.aspects;

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

/**
 * 	Represents a reference to a method, used in a pointcut description. References to methods are always fully qualified and
 * 	contain both the return type of the method and the formal parameters.
 *
 * 	@author jensdt
 *
 */
public class MethodReference<E extends MethodReference<E>> extends NamespaceElementImpl<E, Element> {
	
	public MethodReference(TypeReference type, QualifiedMethodHeader fqn) {
		setType(type);
		setFqn(fqn);
	}
	
	/**
	 *	Return type of the method 	
	 */
	private SingleAssociation<MethodReference<E>, TypeReference> _type = new SingleAssociation<MethodReference<E>, TypeReference>(this);
	
	private void setType(TypeReference type) {
		setAsParent(_type, type);
	}
	
	private TypeReference type() {
		return _type.getOtherEnd();
	}

	/**
	 * 	The fully qualified name of the method
	 */
	private SingleAssociation<MethodReference<E>, QualifiedMethodHeader> _fqn = new SingleAssociation<MethodReference<E>, QualifiedMethodHeader>(this);

	private void setFqn(QualifiedMethodHeader fqn) {
		setAsParent(_fqn, fqn);
	}

	private QualifiedMethodHeader fqn() {
		return _fqn.getOtherEnd();
	}

	/**
	 * 	Return the signature of this method
	 */
	public Signature signature() {
		return fqn().signature(); // TODO: this is used to select the joinpoints, need to figure this out with packages etc
	}


	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		result.add(type());
		result.add(fqn());
		
		return result;
	}

	@Override
	public E clone() {
		return (E) new MethodReference<E>(type().clone(), (QualifiedMethodHeader) fqn().clone());
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

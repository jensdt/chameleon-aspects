package chameleon.aspects.pointcut;

import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.Signature;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.validation.VerificationResult;

public class PointcutHeader<E extends PointcutHeader<E>> extends
		DeclarationWithParametersHeader<E, NamespaceElement, PointcutSignature> {

	public PointcutHeader(String name) {
		setName(name);
	}
	
	public E clone() {
		return super.clone();
	}

	public String getName() {
		return _name;
	}

	private String _name;

	@Override
	public PointcutSignature signature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		this._name = name;
	}

	@Override
	public E createFromSignature(Signature signature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected E cloneThis() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VerificationResult verifySelf() {
		// TODO Auto-generated method stub
		return null;
	}
}

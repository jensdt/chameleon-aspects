package chameleon.aspects.pointcut;

import java.util.List;

import chameleon.core.Config;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.Signature;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.SimpleNameMethodSignature;

public class PointcutHeader<E extends PointcutHeader<E>> extends
		DeclarationWithParametersHeader<E, NamespaceElement, PointcutSignature> {

	public PointcutHeader(String name) {
		setName(name);
	}

	public String getName() {
		return _name;
	}

	private String _name;

	private PointcutSignature _signatureCache;
	
	@Override
	public PointcutSignature signature() {
		PointcutSignature result;
		boolean cacheSignatures = Config.cacheSignatures();
		if(cacheSignatures) {
		  result = _signatureCache;
		} else {
			result = null;
		}
		if(result == null) {
			result = new PointcutSignature(getName());
			result.setUniParent(parent());
			for(FormalParameter param: formalParameters()) {
				result.add(param.clone());
			}
			if(cacheSignatures) {
				_signatureCache = result;
			}
		}
		return result;
	}
	
	@Override
	public void flushLocalCache() {
		_signatureCache = null;
	}

	@Override
	public void setName(String name) {
		this._name = name;
	}

	@Override
	public E createFromSignature(Signature signature) {
		if(signature instanceof PointcutSignature) {
			PointcutSignature sig = (PointcutSignature) signature;
			E result;
			List<FormalParameter> sigParams = sig.formalParemeters();
			List<FormalParameter> params = formalParameters();
			int size = params.size();
			if(sigParams.size() != size) {
				throw new ChameleonProgrammerException();
			} else {
				// clone and copy parameters
				result = clone();
				result.setName(sig.name());
				params = result.formalParameters();
				for(int i=0; i <size; i++) {
					params.get(i).setTypeReference(sigParams.get(i).getTypeReference().clone());
					params.get(i).setName(sigParams.get(i).getName());
				}
			}
			return result;
		} else {
  		throw new ChameleonProgrammerException("Setting wrong type of signature. Provided: "+(signature == null ? null :signature.getClass().getName())+" Expected PointcutSignature");
		}
	}

	@Override
	protected E cloneThis() {
		return (E) new PointcutHeader(name());
	}

	@Override
	public VerificationResult verifySelf() {
		// TODO Auto-generated method stub
		return Valid.create();
	}
}

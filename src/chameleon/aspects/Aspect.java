package chameleon.aspects;

import java.util.List;

import org.rejuse.association.MultiAssociation;

import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.VerificationResult;

public class Aspect<E extends Aspect> extends NamespaceElementImpl<E, Element> {
	/**
	 * 	Get the list of pointcuts that have been defined in this Aspect
	 */
	public List<Pointcut> pointcuts() {
		return _pointcuts.getOtherEnds();
	}
	
	private MultiAssociation<Aspect, Pointcut> _pointcuts = new MultiAssociation<Aspect, Pointcut>(this);
	
	public MultiAssociation<Aspect, Pointcut> pointcutLink() {
		return _pointcuts;
	}
	
	/**
	 * 	Get the list of advices that have been defined in this Aspect
	 */
	public List<Advice> advices() {
		return _advices.getOtherEnds();
	}
	
	private MultiAssociation<Aspect, Advice> _advices = new MultiAssociation<Aspect, Advice>(this);
	
	public MultiAssociation<Aspect, Advice> adviceLink() {
		return _advices;
	}

	@Override
	public List<? extends Element> children() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E clone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VerificationResult verifySelf() {
		// TODO Auto-generated method stub
		return null;
	}

}

package chameleon.aspects;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.reference.CrossReferenceImpl;
import chameleon.core.reference.SimpleReference;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.util.Util;

public class Advice<E extends Advice<E>> extends NamespaceElementImpl<E, Element> {

	public Advice() {
		
	}

	private SingleAssociation<Advice<E>, Element> _body = new SingleAssociation<Advice<E>, Element>(this);
	private SingleAssociation<Advice<E>, SimpleReference<Pointcut>> _pointcutReference = new SingleAssociation<Advice<E>, SimpleReference<Pointcut>>(this);
	
	public Element body() {
		return _body.getOtherEnd();
	}
	
	public void setBody(Element element) {
		setAsParent(_body, element);
	}
	
	/**
	 * 	Get the Aspect this Advice belongs to
	 */
	public Aspect aspect() {
		return (Aspect) parentLink().getOtherEnd();
	}
	
	private Pointcut pointcut;
	
	/**
	 * 	Get the pointcut that this Advice applies to
	 */
	public Pointcut pointcut() {
		try {
			return pointcutReference().getElement();
		} catch (LookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}

	private SimpleReference<Pointcut> pointcutReference() {
		return _pointcutReference.getOtherEnd();
	}

	public void setPointcutReference(SimpleReference<Pointcut> pointcutref)  {
		setAsParent(_pointcutReference, pointcutref);
	}
	

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(body(), result);
		Util.addNonNull(pointcutReference(), result);
		
		return result;
	}

	@Override
	public E clone() {
		Advice clone = new Advice();
		clone.setPointcutReference(pointcutReference().clone());
		clone.setBody(body().clone());
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
			
		return result;
	}
}

package chameleon.aspects;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.VerificationResult;

public class Advice<E extends Advice> extends NamespaceElementImpl<E, Element> {
	
	public Advice() {
		
	}
	
	public Advice(Aspect aspect) {
		setAspect(aspect);
	}
	
	public void setAspect(Aspect other) {
		if (other != null)
			parentLink().connectTo(other.adviceLink());
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
		return pointcut;
	}
	
	/**
	 * 	Set the pointcut that this Advice applies to
	 */
	public void setPointcut(Pointcut pointcut)  {
		try {
			if (pointcut == null ||
				!pointcut.aspect().sameAs(aspect()))
				throw new IllegalArgumentException();
		} catch (LookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.pointcut = pointcut;
	}
	
	private Element body;
	
	/**
	 * 	Get the body of the advice
	 */
	public Element body() {
		return body;
	}

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public E clone() {
		Advice clone = new Advice();
		clone.setPointcut(pointcut().clone());
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		// TODO Auto-generated method stub
		return null;
	}
}

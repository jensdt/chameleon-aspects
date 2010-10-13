package chameleon.aspects;

import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.VerificationResult;

public abstract class Advice<E extends Advice> extends NamespaceElementImpl<E, Element> { 
	public Advice(Aspect aspect) {
		setAspect(aspect);
	}
	
	public void setAspect(Aspect other) {
		if (other != null)
			_aspect.connectTo(other.pointcutLink());
		else
			_aspect.connectTo(null);
	}
	
	private SingleAssociation<Advice, Aspect> _aspect = new SingleAssociation(this);
	
	/**
	 * 	Get the Aspect this Advice belongs to
	 */
	public Aspect aspect() {
		return _aspect.getOtherEnd();
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
	 * @throws LookupException TODO
	 */
	public void setPointcut(Pointcut pointcut) throws LookupException {
		if (pointcut == null ||
			!pointcut.aspect().sameAs(aspect()))
			throw new IllegalArgumentException();
		
		this.pointcut = pointcut;
	}
	
	private Element body;
	
	/**
	 * 	Get the body of the advice
	 */
	public Element body() {
		return body;
	}
}

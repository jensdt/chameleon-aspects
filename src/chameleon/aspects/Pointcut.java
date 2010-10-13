package chameleon.aspects;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;

/**
 *
 *	A Pointcut picks out joinpoints in the program flow.
 *
 *	TODO: more doc
 * 	
 * 	@author Jens De Temmerman
 *
 */
public abstract class Pointcut<E extends Pointcut> extends NamespaceElementImpl<E, Element> {
	public Pointcut(Aspect aspect) {
		setAspect(aspect);
	}
	
	public Pointcut(Aspect aspect, String name) {
		this(aspect);
		this.name = name;
	}
	
	private SingleAssociation<Pointcut, Aspect> _aspect = new SingleAssociation(this);
	
	public void setAspect(Aspect other) {
		if (other != null)
			_aspect.connectTo(other.pointcutLink());
		else
			_aspect.connectTo(null);
	}
	
	/**
	 * 	Get the Aspect that this Pointcut belongs to
	 */
	public Aspect aspect() {
		return _aspect.getOtherEnd();
	}
	
	/**
	 * 	The name of the pointcut, so several advices may reference it without having to copy/paste the pointcut definition.
	 * 	Must be unique in the Aspect.
	 */
	private String name;
	
	
	/**
	 * 	Return the (optional) name of this pointcut
	 */
	public String name() {
		return name;
	}
}

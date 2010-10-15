package chameleon.aspects;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;
import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.MethodHeader;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.reference.CrossReference;

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
	
	public Pointcut() {
		
	}
	
	public Pointcut(Aspect aspect) {
		setAspect(aspect);
	}
	
	public Pointcut(Aspect aspect, String name) {
		this(aspect);
		setName(name);
	}
	
	public void setAspect(Aspect other) {
		if (other != null)
			parentLink().connectTo(other.pointcutLink());
	}
	
	/**
	 * 	Get the Aspect that this Pointcut belongs to
	 */
	public Aspect aspect() {
		return (Aspect) parentLink().getOtherEnd();
	}
	
	public abstract List<? extends Element> joinpoints() throws LookupException;
	
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
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public abstract E clone();
}

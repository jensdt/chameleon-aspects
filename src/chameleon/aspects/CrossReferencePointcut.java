package chameleon.aspects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.MethodHeader;
import chameleon.core.reference.CrossReference;
import chameleon.core.validation.VerificationResult;

public class CrossReferencePointcut<E extends CrossReferencePointcut> extends Pointcut<E> {

	public CrossReferencePointcut() {
		super();
	}
	
	public CrossReferencePointcut(Aspect aspect) {
		super(aspect);
	}
	
	public CrossReferencePointcut(Aspect aspect, String name) {
		super(aspect, name);
	}
	
	/**
	 * 	This list contains all the method headers that are viable joinpoints for this pointcut. In the current implementation
	 * 	we limit the CR-pointcut description to just disjunctions of method calls (e.g. A.x() OR B.y()) 
	 */
	private List<MethodHeader> joinpointNames = new ArrayList<MethodHeader>();
	
	public void addJoinpoint(MethodHeader joinpoint) {
		if (joinpoint != null)
			joinpointNames.add(joinpoint);
	}
	
	@Override
	public List<? extends CrossReference> joinpoints() throws LookupException {
		return language().defaultNamespace().descendants(CrossReference.class, 
				new UnsafePredicate<CrossReference, LookupException>() {

					@Override
					public boolean eval(final CrossReference cr) throws LookupException {
						return new UnsafePredicate<MethodHeader, LookupException>() {

							@Override
							public boolean eval(MethodHeader hs)
									throws LookupException {
								return cr != null && cr.getElement().sameAs(hs);
							}
							
						}.exists(joinpointNames);
					}

			
				}
		
		);
	}

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public E clone() {
		CrossReferencePointcut clone = new CrossReferencePointcut();
		clone.setName(name());
		
		for (MethodHeader jp : joinpointNames) {
			clone.addJoinpoint(jp);
		}
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		// TODO Auto-generated method stub
		return null;
	}

}

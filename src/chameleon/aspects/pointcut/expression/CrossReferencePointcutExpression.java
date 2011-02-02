package chameleon.aspects.pointcut.expression;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.MethodReference;
import chameleon.core.element.Element;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.reference.CrossReference;
import chameleon.oo.type.Type;

public class CrossReferencePointcutExpression<E extends CrossReferencePointcutExpression<E>> extends PointcutExpression<E> {
	private SingleAssociation<CrossReferencePointcutExpression, MethodReference> _methodReference = new SingleAssociation<CrossReferencePointcutExpression, MethodReference>(this);
	
	public CrossReferencePointcutExpression(MethodReference methodReference) {
		setMethodReference(methodReference);
	}

	private void setMethodReference(MethodReference methodReference) {
		setAsParent(_methodReference, methodReference);
	}
	
	private MethodReference methodReference() {
		return _methodReference.getOtherEnd();
	}

	@Override
	public boolean matches(Element joinpoint) throws LookupException {
		if (!(joinpoint instanceof MethodInvocation))
			return false;
		
		Method e = ((MethodInvocation) joinpoint).getElement();
		
		// Check if the type matches
		if (!sameAsWithWildcard(e.returnTypeReference().getType().signature().name(), methodReference().type()))
			return false;
				
		
		// Check if the signature matches
		if (!sameAsWithWildcard(e.signature().name(), methodReference().signature().name()))
			return false;
		
		// Check if the FQN matches
		
		String jpFqn = ((Type) e.nearestAncestor(Type.class)).getFullyQualifiedName();
		String definedFqn = methodReference().getFullyQualifiedName();

		return sameFQNWithWildcard(jpFqn, definedFqn);
	}
	
	/**
	 * 	Match rules:
	 * 			hrm.Person matches with:
	 * 				- hrm.Person (or any wildcard combo, e.g. h*m.P*)
	 * 				- **.Person  (or any wildcard combo, e.g. **.P*)
	 * 				- hrm.**     (or any wildcard combo, e.g. h*m.**)
	 * 				- **.**
	 * 				- **
	 * 
	 * @param jpFqn_
	 * @param definedFqn_
	 * @return
	 */
	private boolean sameFQNWithWildcard(String jpFqn_, String definedFqn_) {
		String[] jpFqn = jpFqn_.split("\\.");
		String[] definedFqn = definedFqn_.split("\\.");
		
		// Special case: if the FQN of the call is a complete wildcard, match everything
		if (definedFqn.length == 1 && definedFqn[0].equals("**"))
			return true;
		
		if (jpFqn.length != definedFqn.length)
			return false;
		
		for (int i = 0; i < jpFqn.length; i++)
			if (!sameAsWithWildcard(jpFqn[i], definedFqn[i]))
				return false;
		
		return true;
	}

	/**
	 * 	Check if s1 is the same as s2 - s2 can contain a wildcard (** = any charater 0 or more times), s1 can
	 *  contain the wildchard character but it will not be treated as a wildcard.
	 *  
	 */
	public boolean sameAsWithWildcard(String s1, String s2) {
		// Turn s2 into a regexp. We convert the wildcard character (**) to a regexp-wildcard (.*) and treat
		// all the rest as a literal (between \Q and \E)
		String regexp = "\\Q" + s2.replace("**", "\\E(.*)\\Q") + "\\E"; 
		
		return s1.matches(regexp);
	}

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		if (methodReference() != null)
			result.add(methodReference());
		
		return result;
	}

	@Override
	public E clone() {
		return (E) new CrossReferencePointcutExpression<E>(methodReference().clone()); 
	}
	
}
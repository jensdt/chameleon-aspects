package chameleon.aspects.pointcutexpression;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;

public abstract class PointcutExpression<E extends PointcutExpression<E>> extends NamespaceElementImpl<E, Element> {
	/**
	 * 	Check if this pointcut expression matches the given joinpoint. Note: null (as a pointcutexpression) always matches.
	 * 
	 * 	@param joinpoint
	 * 			The joinpoint to check
	 * 	@return True if the pointcut expression matches the joinpoint, false otherwise
	 * @throws LookupException 
	 */
	public abstract boolean matches(Element joinpoint) throws LookupException;

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
	
	public abstract E clone();
}

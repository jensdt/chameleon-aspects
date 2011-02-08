package chameleon.aspects.pointcut.expression;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;

public abstract class PointcutExpression<E extends PointcutExpression<E>> extends NamespaceElementImpl<E> {
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

	public boolean hasParameter(FormalParameter fp) {
		return false; // TODO: make this abstract and add sufficient implementation
	}
}

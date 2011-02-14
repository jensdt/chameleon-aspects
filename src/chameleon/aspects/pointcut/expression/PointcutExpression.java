package chameleon.aspects.pointcut.expression;

import chameleon.aspects.pointcut.MatchResult;
import chameleon.aspects.pointcut.Pointcut;
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
	 * @throws LookupException 
	 */
	public abstract MatchResult matches(Element joinpoint) throws LookupException;

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
	
	public Pointcut pointcut() {
		return nearestAncestor(Pointcut.class);
	}
	
	public abstract E clone();

	public boolean hasParameter(FormalParameter fp) {
		return false; // TODO: make this abstract and add sufficient implementation
	}
}

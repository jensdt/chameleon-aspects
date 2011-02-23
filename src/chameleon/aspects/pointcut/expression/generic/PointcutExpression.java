package chameleon.aspects.pointcut.expression.generic;

import java.util.Set;

import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;

public abstract class PointcutExpression<E extends PointcutExpression<E, T>, T extends Element> extends NamespaceElementImpl<E> {
	/**
	 * 	Check if this pointcut expression matches the given joinpoint. Note: null (as a pointcutexpression) always matches.
	 * 
	 * 	@param joinpoint
	 * 			The joinpoint to check
	 * @throws LookupException 
	 */
	public abstract MatchResult matches(T joinpoint) throws LookupException;

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
	
	public abstract Set<Class> supportedJoinpoints();
	
	/**
	 * 	Check if a given class is supported. A class
	 * 	is supported if its supertype is supported
	 * 
	 * 	@param c	The class to check
	 * 	@return	True if the class is supported, false otherwise
	 */
	public boolean isSupported(Class c) {
		
		for (Class supported : supportedJoinpoints()) {
			if (supported.isAssignableFrom(c))
				return true;
		}
		
		return false;
	}
}

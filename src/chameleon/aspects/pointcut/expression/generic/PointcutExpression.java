package chameleon.aspects.pointcut.expression.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;

/**
 * 	Represents a pointcut expression, a building block for pointcuts
 * 
 * 	@author Jens
 *
 * 	@param <E>
 * 	@param <T>
 */
public abstract class PointcutExpression<E extends PointcutExpression<E>> extends NamespaceElementImpl<E> {
	/**
	 * 	Check if this pointcut expression matches the given joinpoint. Note: null (as a pointcutexpression) always matches.
	 * 
	 * 	@param joinpoint
	 * 			The joinpoint to check
	 * @throws LookupException 
	 */
	public abstract MatchResult matches(Element joinpoint) throws LookupException;

	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		MatchResult matches = matches(joinpoint);
		
		if (matches.isMatch())
			return MatchResult.noMatch();
		else
			return new MatchResult(this, joinpoint);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
	
	/**
	 * 	Get the pointcut definition this expression belongs to
	 * 
	 * 	@return	The nearest pointcut
	 */
	public Pointcut pointcut() {
		return nearestAncestor(Pointcut.class);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public abstract E clone();

	/**
	 * 	Get the list of element types that are supported as joinpoints for this PointcutExpression.
	 * 
	 * 	E.g. A joinpoint that matches method invocations has returns {MethodInvocation.class}
	 * 
	 * 	@return	The list of supported types as joinpoint
	 */
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
	
	/**
	 * 	Get this pointcut-expression tree but filter the types to the given type (only instances of the supplied type remain in the tree)
	 * 
	 * 	@param 	type
	 * 			The type to select
	 * 	@return	The pruned tree
	 */
	public PointcutExpression getPrunedTree(Class<? extends PointcutExpression> type) {
		if (type.isAssignableFrom(getClass())) {
			PointcutExpression clone = clone();
			clone.setOrigin(origin());
			
			return clone;
		}
		else
			return null;
	}

	/**
	 * 	Get this pointcut-expression tree but filter the types to any type but the given type (all instances of the supplied type are removed)
	 * 
	 * 	@param 	type
	 * 			The type to exclude
	 * 	@return	The pruned tree
	 */
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type) {
		if (type.isAssignableFrom(getClass()))
			return null;
		else {
			PointcutExpression clone = clone();
			clone.setOrigin(origin());
			
			return clone;
		}
	}

	public List<? extends PointcutExpression<?>> asList() {
		return Collections.<PointcutExpression<?>>singletonList(this);
	}
	
	/**
	 * 	Check if this pointcut expression has the specified parameter
	 * 
	 * 	@param 	fp
	 * 			The parameter to check
	 * 	@return	True if the parameter can be resolved, false otherwise
	 */
	public boolean hasParameter(FormalParameter fp) {
		return false;
	}
}

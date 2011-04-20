package chameleon.aspects.pointcut.expression.staticexpression;

import java.util.Set;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;


public interface StaticPointcutExpression<E extends StaticPointcutExpression<E>> extends PointcutExpression<E> {
	/**
	 * 	Check if this pointcut expression matches the given joinpoint. Note: null (as a pointcutexpression) always matches.
	 * 
	 * 	@param joinpoint
	 * 			The joinpoint to check
	 * @throws LookupException 
	 */
	public MatchResult matches(Element joinpoint) throws LookupException;
	
	/**
	 * 	Get the list of element types that are supported as joinpoints for this PointcutExpression.
	 * 
	 * 	E.g. A joinpoint that matches method invocations has returns {MethodInvocation.class}
	 * 
	 * 	@return	The list of supported types as joinpoint
	 */
	public Set<Class<? extends Element>> supportedJoinpoints();
	
	public MatchResult matchesInverse(Element joinpoint) throws LookupException;
}

package chameleon.aspects.pointcut.expression.staticexpression;

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
	 * 	Check if a given class is supported. A class
	 * 	is supported if its supertype is supported
	 * 
	 * 	@param c	The class to check
	 * 	@return	True if the class is supported, false otherwise
	 */
	public boolean isSupported(Class<? extends Element> type);
}

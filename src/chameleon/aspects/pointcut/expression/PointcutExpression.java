package chameleon.aspects.pointcut.expression;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.PointcutReference;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.variable.FormalParameter;

public interface PointcutExpression<E extends PointcutExpression<E>> extends NamespaceElement<E> {
	/**
	 * 	Get the pointcut definition this expression belongs to
	 * 
	 * 	// FIXME: check this, can be null. Where/how is this used?
	 * 
	 * 	@return	The nearest pointcut
	 */
	public Pointcut pointcut();
	
	/**
	 * 	Get the list of element types that are supported as joinpoints for this PointcutExpression.
	 * 
	 * 	E.g. A joinpoint that matches method invocations has returns {MethodInvocation.class}
	 * 
	 * 	@return	The list of supported types as joinpoint
	 */
	public Set<Class<? extends Element>> supportedJoinpoints();
	
	/**
	 * 	Check if a given class is supported. A class
	 * 	is supported if its supertype is supported
	 * 
	 * 	@param c	The class to check
	 * 	@return	True if the class is supported, false otherwise
	 */
	public boolean isSupported(Class c);
	
	/**
	 * 	Get this pointcut-expression tree but filter according to the given predicate
	 * 
	 * 	@param 	filter
	 * 			The predicate to filter
	 * 	@return	The pruned tree
	 */
	public PointcutExpression<?> getPrunedTree(SafePredicate<PointcutExpression<?>> filter);
	
	/**
	 * 	Get this pointcut-expression tree but filter according to the given type
	 * 
	 * 	@param 	filter
	 * 			The predicate to filter
	 * 	@return	The pruned tree
	 */
	public PointcutExpression<?> getPrunedTree(Class<?> type);
	
	/**
	 * 	Get this pointcut-expression tree but filter the types to any type but the given type (all instances of the supplied type are removed)
	 * 
	 * 	@param 	type
	 * 			The type to exclude
	 * 	@return	The pruned tree
	 */
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type);
	
	public List<? extends PointcutExpression<?>> asList();
	
	/**
	 * 	Check if this pointcut expression has the specified parameter
	 * 
	 * 	@param 	fp
	 * 			The parameter to check
	 * 	@return	True if the parameter can be resolved, false otherwise
	 */
	public boolean hasParameter(FormalParameter fp);
	
	
	public List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException;
	
	public List<PointcutExpression> toPostorderList();

	public PointcutExpression<?> expand();
}
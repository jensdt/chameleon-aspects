package chameleon.aspects.pointcut.expression;

import java.util.List;

import org.rejuse.predicate.SafePredicate;

import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.variable.FormalParameter;

public interface PointcutExpression<E extends PointcutExpression<E>> extends NamespaceElement<E> {	
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
	public PointcutExpression<?> getPrunedTree(Class<? extends PointcutExpression> type);
	
	/**
	 * 	Get this pointcut-expression tree but filter out the types to any type but the given type (all instances of the supplied type are removed)
	 * 
	 * 	@param 	type
	 * 			The type to exclude
	 * 	@return	The pruned tree
	 */
	public PointcutExpression<?> removeFromTree(Class<? extends PointcutExpression> type);
	
	/**
	 * 	Get this pointcut-expression tree but filter out according to the given filter
	 * 
	 * 	@param 	filter
	 * 			The predicate to filter
	 * 	@return	The pruned tree
	 */
	public PointcutExpression<?> removeFromTree(SafePredicate<PointcutExpression<?>> filter);
	
	/**
	 * 	Check if this pointcut expression has the specified parameter
	 * 
	 * 	@param 	fp
	 * 			The parameter to check
	 * 	@return	True if the parameter can be resolved, false otherwise
	 */
	public boolean hasParameter(FormalParameter fp);
	
	/**
	 * 	Get all the joinpoints in the given compilation unit that this pointcut expression selects
	 * 	
	 * 	@param 	compilationUnit
	 * 			The compilationunit to check
	 * 	@return	The list of matchresults (joinpoint and pointcut expression)
	 * 	
	 * 	@throws LookupException TODO: check if this is necessary
	 */
	public List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException;
	
	/**
	 * 	Get the pointcut expression tree as a list, in post order
	 * 
	 * 	@return	The pointcut expression as a tree in post order
	 */
	public List<PointcutExpression<?>> toPostorderList();

	/**
	 * 	Expand the pointcut expression - replace each reference to a named pointcut with the expression itself
	 *
	 * 	@return	The expanded pointcut expression
	 */
	public PointcutExpression<?> expand();
}
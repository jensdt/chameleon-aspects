package chameleon.aspects.pointcut.expression.dynamicexpression;

import java.util.Map;

import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.variable.FormalParameter;

public interface ParameterExposurePointcutExpression<E extends ParameterExposurePointcutExpression<E>> extends PointcutExpression<E> {
	/**
	 * 	Get the index of the given formal parameter in this pointcut expression
	 * 
	 * 	@param 	fp
	 * 			The parameter to get the index of
	 * 	@return	The index of the parameter if the parameter can be resolved, -1 otherwise
	 */
	public int indexOfParameter(FormalParameter fp);
	
	/**
	 * 	Find the expression that exposes the given parameter
	 * 
	 * 	@param 	formalParemeter
	 * 			The parameter to look for
	 * 	
	 * 	@return	The expression that exposes the given parameter
	 */
	public ParameterExposurePointcutExpression<?> findExpressionFor(FormalParameter formalParameter);
	
	/**
	 * 	Rename the parameters this expression exposes according to the given map
	 * 
	 * 	@param 	parameterNamesMap
	 * 			The map to rename the parameters (keys = from, corresponding values = to)
	 */
	public void renameParameters(Map<String, String> parameterNamesMap);
}

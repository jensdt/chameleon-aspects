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
	
	public ParameterExposurePointcutExpression<?> findExpressionFor(FormalParameter fp);
	
	public void renameParameters(Map<String, String> parameterNamesMap);
}

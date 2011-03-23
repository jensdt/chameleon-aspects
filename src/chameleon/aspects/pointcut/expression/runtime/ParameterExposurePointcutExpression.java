package chameleon.aspects.pointcut.expression.runtime;

import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.variable.FormalParameter;

public interface ParameterExposurePointcutExpression {
	/**
	 * 	Check if this pointcut expression has the specified parameter
	 * 
	 * 	@param 	fp
	 * 			The parameter to check
	 * 	@return	True if the parameter can be resolved, false otherwise
	 */
	public boolean hasParameter(FormalParameter fp);
	/**
	 * 	Get the index of the given formal parameter in this pointcut expression
	 * 
	 * 	@param 	fp
	 * 			The parameter to get the index of
	 * 	@return	The index of the parameter if the parameter can be resolved, -1 otherwise
	 */
	public int indexOfParameter(FormalParameter fp);
	public Element origin();
}

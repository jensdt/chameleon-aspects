package chameleon.aspects.advice.runtimetransformation;

import chameleon.core.element.Element;

/**
 * 	Represents a coordinator responsible for transforming advice after it has been created, e.g. for the dynamic insertion of runtime checks
 * 
 * 	@author Jens
 *
 * 	@param <T>	The type of the advice that is being transformed (e.g. NormalMethod for MethodInvocations)
 */
public interface Coordinator<T extends Element<?>> {
	/**
	 * 	Transform the given advice element to add all applicable runtime checks
	 * 
	 * 	@param element	The advice element
	 */
	public void transform(T element);
}

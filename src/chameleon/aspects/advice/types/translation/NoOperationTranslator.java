package chameleon.aspects.advice.types.translation;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.RuntimeTransformer;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	Represents a transformation that doesn't do anything.
 * 
 * 	@author Jens
 *
 */
public class NoOperationTranslator extends AbstractAdviceTransformationProvider<Element> {

	public NoOperationTranslator() {
		super(null);
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	No operation, so don't do anything
	 */
	@Override
	public Element transform(Advice<?> advice) throws LookupException {
		return null;
	}

	@Override
	public boolean canTransform(RuntimePointcutExpression pointcutExpression) {
		return false;
	}

	@Override
	public RuntimeTransformer getRuntimeTransformer(RuntimePointcutExpression pointcutExpression) {
		return null;
	}

}
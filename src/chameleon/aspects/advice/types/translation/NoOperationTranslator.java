package chameleon.aspects.advice.types.translation;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

/**
 * 	Represents a transformation that doesn't do anything.
 * 
 * 	@author Jens
 *
 */
public class NoOperationTranslator extends AbstractAdviceTransformationProvider<Element> {

	/**
	 * 	Default constructor
	 */
	public NoOperationTranslator() {
		super(null, null);
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	No operation, so don't do anything
	 */
	@Override
	public Element transform(WeavingEncapsulator next) throws LookupException {
		return null;
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public boolean supports(RuntimePointcutExpression pointcutExpression) {
		return false;
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public RuntimeExpressionProvider getRuntimeTransformer(RuntimePointcutExpression pointcutExpression) {
		return null;
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected Coordinator<Element> getCoordinator(WeavingEncapsulator previousEncapsulator, WeavingEncapsulator nextEncapsulator) {
		return null;
	}
}
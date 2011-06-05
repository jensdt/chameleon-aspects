package chameleon.aspects.pointcut.expression;

import java.util.Collections;
import java.util.List;

import org.rejuse.predicate.SafePredicate;

import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;

/**
 * 	Represents a pointcut expression, a building block for pointcuts
 * 
 * 	@author Jens
 *
 * 	@param <E>
 * 	@param <T>
 */
public abstract class AbstractPointcutExpression<E extends AbstractPointcutExpression<E>> extends NamespaceElementImpl<E> implements PointcutExpression<E> {
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public abstract E clone();
		
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> getPrunedTree(final Class<? extends PointcutExpression> type) {
		SafePredicate<PointcutExpression<?>> filter = new SafePredicate<PointcutExpression<?>>() {

			@Override
			public boolean eval(PointcutExpression<?> object) {
				return type.isInstance(object);
			}
		};
		
		return getPrunedTree(filter);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> getPrunedTree(SafePredicate<PointcutExpression<?>> filter) {
		if (filter.eval(this)) {
			PointcutExpression<?> clone = clone();
			clone.setOrigin(origin());
			
			return clone;
		} 
		else
			return null;
	}

	/**
	 *	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> removeFromTree(final Class<? extends PointcutExpression> type) {	
		SafePredicate<PointcutExpression<?>> filter = new SafePredicate<PointcutExpression<?>>() {

			@Override
			public boolean eval(PointcutExpression<?> object) {
				return type.isInstance(object);
			}
		};
		
		return removeFromTree(filter);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> removeFromTree(SafePredicate<PointcutExpression<?>> filter) {
		if (filter.eval(this))
			return null;
		else {
			PointcutExpression<?> clone = clone();
			clone.setOrigin(origin());
			
			return clone;
		}
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public List<PointcutExpression<?>> toPostorderList() {
		return Collections.<PointcutExpression<?>>singletonList(this);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		return this.clone();
	}
	
	/**
	 *	{@inheritDoc}
	 */
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return false;
	}
}
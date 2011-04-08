package chameleon.aspects.pointcut.expression.namedpointcut;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.PointcutReference;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.ParameterExposurePointcutExpression;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.variable.FormalParameter;
import chameleon.util.Util;

public class NamedPointcutExpression<E extends NamedPointcutExpression<E>> extends PointcutExpression<E> implements CrossReference<E, Pointcut>, ParameterExposurePointcutExpression {
	
	private SingleAssociation<NamedPointcutExpression<E>, PointcutReference> _pointcutReference = new SingleAssociation<NamedPointcutExpression<E>, PointcutReference>(this);
	
	public void setPointcutReference(PointcutReference ref) {
		setAsParent(_pointcutReference, ref);
	}
	
	public PointcutReference<?> pointcutReference() {
		return _pointcutReference.getOtherEnd();
	}

	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(pointcutReference());
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		if (pointcutReference() == null)
			return MatchResult.noMatch();
		
		MatchResult result = pointcutReference().getElement().expression().matches(joinpoint);
		
		if (result.isMatch()) {
			return new MatchResult(this, joinpoint);
		}
		
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		NamedPointcutExpression clone = new NamedPointcutExpression<E>();
		
		if (pointcutReference() != null)
			clone.setPointcutReference(pointcutReference().clone());
		
		return (E) clone;
	}

	@Override
	public Set<Class> supportedJoinpoints() {
		if (pointcutReference() == null)
			return Collections.emptySet();
		
		try {
			return pointcutReference().getElement().expression().supportedJoinpoints();
		} catch (LookupException e) {
			return Collections.emptySet();
		}
	}

	@Override
	public Pointcut getElement() throws LookupException {
		if (pointcutReference() == null)
			return null;
		
		return pointcutReference().getElement();
	}

	@Override
	public Declaration getDeclarator() throws LookupException {
		if (pointcutReference() == null)
			return null;
		
		return pointcutReference().getDeclarator();
	}

	/**
	 * 	{@inheritDoc}
	 */
	public PointcutExpression getPrunedTree(Class<? extends PointcutExpression> type) {
		if (pointcutReference() == null)
			return null;
		
		try {
			return pointcutReference().getElement().expression().getPrunedTree(type);
		} catch (LookupException ex) {
			return null;
		}
	}

	/**
	 * 	{@inheritDoc}
	 */
	public PointcutExpression removeFromTree(Class<? extends PointcutExpression> type) {
		if (pointcutReference() == null)
			return null;
		
		try {
			return pointcutReference().getElement().expression().removeFromTree(type);
		} catch (LookupException ex) {
			return null;
		}
	}

	public List<? extends PointcutExpression<?>> asList() {
		return Collections.<PointcutExpression<?>>singletonList(this);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public boolean hasParameter(FormalParameter fp) {
		return pointcutReference().hasParameter(fp);
	}
	
	@Override
	public int indexOfParameter(FormalParameter fp) {
		// Step one: identify at which index no. this parameter is
		int index = pointcutReference().indexOfParameter(fp);
		if (index == -1)
			return -1;
		
		// Step two: get the parameter at that index of the referenced pointcut definition
		try {
			FormalParameter referencedParameter = (FormalParameter) pointcutReference().getElement().parameters().get(index);
			
			// Step three: find that parameter in the expression of the referenced pointcut definition
			return pointcutReference().getElement().expression().indexOfParameter(referencedParameter);
		} catch (LookupException e) {
			return -1;
		}
	}
}
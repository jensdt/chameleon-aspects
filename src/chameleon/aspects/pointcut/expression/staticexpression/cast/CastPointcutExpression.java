package chameleon.aspects.pointcut.expression.staticexpression.cast;

import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.ClassCastExpression;
import chameleon.util.Util;

public class CastPointcutExpression<E extends CastPointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {
	
	public CastPointcutExpression(TypeReference castToType) {
		setCastToType(castToType);
	}

	private SingleAssociation<CastPointcutExpression<E>, TypeReference<?>> _castToType = new SingleAssociation<CastPointcutExpression<E>, TypeReference<?>>(this);
	
	public TypeReference<?> castToType() {
		return _castToType.getOtherEnd();
	}
	
	public void setCastToType(TypeReference<?> castToType) {
		setAsParent(_castToType, castToType);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		if (!((ClassCastExpression) joinpoint).getType().assignableTo(castToType().getType()))
			return MatchResult.noMatch();
		
		return new MatchResult<ClassCastExpression>(this, (ClassCastExpression) joinpoint);
	}
	
	@Override
	public boolean isSupported(Class<? extends Element> c) {
		if (ClassCastExpression.class.isAssignableFrom(c))
			return true;
		
		return false;
	}

	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(castToType());
	}

	@Override
	public E clone() {
		CastPointcutExpression<E> expression = new CastPointcutExpression<E>((castToType() == null) ? null : castToType().clone());
		
		return (E) expression;
	}
}
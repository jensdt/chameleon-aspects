package chameleon.aspects.pointcut.expression.dynamicexpression;

import java.util.Collections;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public abstract class TypePointcutExpression<E extends TypePointcutExpression<E>> extends AbstractParameterExposurePointcutExpression<E> implements RuntimePointcutExpression<E>, ParameterExposurePointcutExpression<E> {
	public TypePointcutExpression(NamedTargetExpression parameter) {
		setParameter(parameter);
	}

	private SingleAssociation<TypePointcutExpression<E>, NamedTargetExpression> _parameter = new SingleAssociation<TypePointcutExpression<E>, NamedTargetExpression>(this);
	
	public NamedTargetExpression parameter() {
		return _parameter.getOtherEnd();
	}
	
	public void setParameter(NamedTargetExpression parameter) {
		setAsParent(_parameter, parameter);
	}
	
	public TypeReference getType() {
		try {
			return new BasicTypeReference(parameter().getType().getFullyQualifiedName());
		} catch (LookupException e) {
			
		}
		
		return null;
	}

	
	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(parameter());
	}
	
	@Override
	public List<NamedTargetExpression> parameters() {
		return Collections.singletonList(parameter());
	}
}
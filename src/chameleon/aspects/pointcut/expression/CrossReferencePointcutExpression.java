package chameleon.aspects.pointcut.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.MethodReference;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;

public class CrossReferencePointcutExpression<E extends CrossReferencePointcutExpression<E>> extends PointcutExpression<E> {
	private SingleAssociation<CrossReferencePointcutExpression, MethodReference> _methodReference = new SingleAssociation<CrossReferencePointcutExpression, MethodReference>(this);
	
	public CrossReferencePointcutExpression(MethodReference methodReference) {
		setMethodReference(methodReference);
	}

	private void setMethodReference(MethodReference methodReference) {
		setAsParent(_methodReference, methodReference);
	}
	
	private MethodReference methodReference() {
		return _methodReference.getOtherEnd();
	}

	@Override
	public boolean matches(Element joinpoint) throws LookupException {
		if (!(joinpoint instanceof CrossReference))
			return false;
		
		return ((CrossReference) joinpoint).getElement().signature().sameAs(methodReference().signature());
	}

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		if (methodReference() != null)
			result.add(methodReference());
		
		return result;
	}

	@Override
	public E clone() {
		return (E) new CrossReferencePointcutExpression<E>(methodReference().clone()); 
	}
	
}

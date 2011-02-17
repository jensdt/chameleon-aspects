package chameleon.aspects.pointcut.expression;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.AnnotationReference;
import chameleon.aspects.pointcut.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.util.Util;

public class AnnotatedMethodInvocationExpression<E extends AnnotatedMethodInvocationExpression<E>> extends PointcutExpression<E> {

	private SingleAssociation<AnnotatedMethodInvocationExpression, AnnotationReference> _reference = new SingleAssociation<AnnotatedMethodInvocationExpression, AnnotationReference>(this); 
	
	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		Util.addNonNull(reference(), result);
		return result;
	}
	
	public AnnotationReference reference() {
		return _reference.getOtherEnd();
	}
	
	public void setReference(AnnotationReference reference) {
		setAsParent(_reference, reference);
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E clone() {
		AnnotatedMethodInvocationExpression<E> clone = new AnnotatedMethodInvocationExpression<E>();
		clone.setReference(reference().clone());
		return (E) clone;
	}

}

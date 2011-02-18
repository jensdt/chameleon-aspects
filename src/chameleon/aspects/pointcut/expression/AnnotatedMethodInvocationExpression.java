package chameleon.aspects.pointcut.expression;

import java.util.ArrayList;
import java.util.List;

import jnome.core.modifier.AnnotationModifier;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.AnnotationReference;
import chameleon.aspects.pointcut.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.modifier.Modifier;
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
		if (!(joinpoint instanceof MethodInvocation))
			return MatchResult.noMatch();
		
		Method target = ((MethodInvocation) joinpoint).getElement();
		
		List<Modifier> modifiers = target.modifiers();
		
		for (Modifier modifier : modifiers) {
			if (modifier instanceof AnnotationModifier) {
				if (((AnnotationModifier) modifier).name().equals(reference().referencendName()))
					return new MatchResult<AnnotatedMethodInvocationExpression, MethodInvocation>(this, (MethodInvocation) joinpoint);
			}
		}
		
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		AnnotatedMethodInvocationExpression<E> clone = new AnnotatedMethodInvocationExpression<E>();
		clone.setReference(reference().clone());
		return (E) clone;
	}

}

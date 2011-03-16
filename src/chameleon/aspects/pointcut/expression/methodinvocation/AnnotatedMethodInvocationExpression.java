package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.modifier.AnnotationModifier;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.modifier.Modifier;
import chameleon.core.variable.FormalParameter;
import chameleon.util.Util;

public class AnnotatedMethodInvocationExpression<E extends AnnotatedMethodInvocationExpression<E, T>, T extends MethodInvocation> extends MethodInvocationPointcutExpression<E, T> {

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
	public MatchResult matches(MethodInvocation joinpoint) throws LookupException {	
		Method target = joinpoint.getElement();
		
		List<Modifier> modifiers = target.modifiers();
		
		for (Modifier modifier : modifiers) {
			if (modifier instanceof AnnotationModifier) {
				if (((AnnotationModifier) modifier).name().equals(reference().referencendName()))
					return new MatchResult<AnnotatedMethodInvocationExpression, MethodInvocation>(this, joinpoint);
			}
		}
		
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		AnnotatedMethodInvocationExpression<E, T> clone = new AnnotatedMethodInvocationExpression<E, T>();
		clone.setReference(reference().clone());
		return (E) clone;
	}
	
	@Override
	public MatchResult matchesInverse(T joinpoint) throws LookupException {
		return super.matchesInverse(joinpoint);
	}
}

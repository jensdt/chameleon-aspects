package chameleon.aspects.pointcut.expression.staticexpression.fieldAccess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.AssignmentExpression;
import chameleon.util.Util;

public class FieldReadPointcutExpression<E extends FieldReadPointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {

	public FieldReadPointcutExpression(TypeReference typeReference, FieldReference reference) {
		setFieldReference(reference);
		setTypeReference(typeReference);
	}
	
	private void setTypeReference(TypeReference typeReference) {
		setAsParent(_typeReference, typeReference);
	}

	private void setFieldReference(FieldReference reference) {
		setAsParent(_fieldReference, reference);
	}

	private SingleAssociation<FieldReadPointcutExpression<E>, FieldReference> _fieldReference = new SingleAssociation<FieldReadPointcutExpression<E>, FieldReference>(this);
	private SingleAssociation<FieldReadPointcutExpression<E>, TypeReference> _typeReference = new SingleAssociation<FieldReadPointcutExpression<E>, TypeReference>(this);
	
	public FieldReference fieldReference() {
		return _fieldReference.getOtherEnd();
	}
	
	public TypeReference typeReference() {
		return _typeReference.getOtherEnd();
	}

	
	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(fieldReference(), result);
		Util.addNonNull(typeReference(), result);
		
		return result;
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	Matching works as follows (consistent with AspectJ):
	 * 
	 * 	The field must be declared in the type referenced by the signature, or a sub type. If it is re-defined in a sub type, it isn't matched.
	 * 
	 * 	E.g.
	 * 
	 * 	Class A : int foo
	 * 	Class B extends A
	 * 
	 *  get(A.foo) matches both a.foo and b.foo
	 *  
	 *  Class A : int foo
	 * 	Class B extends A : int foo
	 * 
	 *  get(A.foo) only matches a.foo
	 *  
	 *  But:
	 *  
	 *  Class A : int foo
	 * 	Class B extends A
	 * 
	 *  get(B.foo) doesn't match a.foo OR b.foo
	 */
	@Override
	public MatchResult matches(Element element) throws LookupException {
		NamedTargetExpression joinpoint = (NamedTargetExpression) element;
		
		if (!(joinpoint.getElement() instanceof RegularMemberVariable))
			return MatchResult.noMatch();
		
		if (joinpoint.parent() instanceof AssignmentExpression && ((AssignmentExpression) joinpoint.parent()).getVariable().sameAs(joinpoint))
				return MatchResult.noMatch();
		
		
		// Typecheck: no inheritance (as AspectJ does it)
		if (!joinpoint.getType().sameAs(typeReference().getType()))
			return MatchResult.noMatch();
		
		// Get the fully qualified name of this field
		String fqn = ((RegularType) joinpoint.getElement().nearestAncestor(RegularType.class)).getFullyQualifiedName() + "." + joinpoint.signature().name();
		
		if (fqn.equals(fieldReference().reference()))
			return new MatchResult(this, joinpoint);
		
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		FieldReference fieldRefClone = null;
		TypeReference typeRefClone = null;
		
		if (fieldReference() != null)
			fieldRefClone = fieldReference().clone();
		
		if (typeReference() != null)
			typeRefClone = typeReference().clone();
		
		return (E) new FieldReadPointcutExpression(typeRefClone, fieldRefClone);
	}

	@Override
	public Set<Class<? extends Element>> supportedJoinpoints() {
		return Collections.<Class<? extends Element>>singleton(NamedTargetExpression.class);
	}

	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}
}

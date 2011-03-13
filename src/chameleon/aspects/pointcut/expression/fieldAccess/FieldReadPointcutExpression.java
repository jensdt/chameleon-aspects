package chameleon.aspects.pointcut.expression.fieldAccess;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.oo.type.RegularType;
import chameleon.support.expression.AssignmentExpression;
import chameleon.support.expression.ThisLiteral;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import chameleon.util.Util;

public class FieldReadPointcutExpression<E extends FieldReadPointcutExpression<E, T>, T extends NamedTargetExpression> extends PointcutExpression<E,T>  {

	public FieldReadPointcutExpression(FieldReference reference) {
		setFieldReference(reference);
	}
	
	private void setFieldReference(FieldReference reference) {
		setAsParent(_fieldReference, reference);
	}

	private SingleAssociation<FieldReadPointcutExpression<E, T>, FieldReference> _fieldReference = new SingleAssociation<FieldReadPointcutExpression<E, T>, FieldReference>(this);
	
	public FieldReference fieldReference() {
		return _fieldReference.getOtherEnd();
	}
	
	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(fieldReference());
	}

	@Override
	public MatchResult matches(T joinpoint) throws LookupException {
		if (!(joinpoint.getElement() instanceof RegularMemberVariable))
			return MatchResult.noMatch();
		
		if (joinpoint.parent() instanceof AssignmentExpression)
			return MatchResult.noMatch();
		
		// Get the fully qualified name of this field
		String fqn = ((RegularType) joinpoint.getElement().nearestAncestor(RegularType.class)).getFullyQualifiedName() + "." + joinpoint.signature().name();
		System.out.println(fqn + " - " + fieldReference().reference());
		if (fqn.equals(fieldReference().reference()))
			return new MatchResult(this, joinpoint);
		
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		return (E) new FieldReadPointcutExpression(fieldReference().clone());
	}

	@Override
	public boolean hasParameter(FormalParameter fp) {
		return false;
	}

	@Override
	public int indexOfParameter(FormalParameter fp) {
		return -1;
	}

	@Override
	public Set<Class> supportedJoinpoints() {
		return Collections.<Class>singleton(NamedTargetExpression.class);
	}

}

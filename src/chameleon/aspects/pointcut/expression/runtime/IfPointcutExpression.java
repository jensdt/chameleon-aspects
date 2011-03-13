package chameleon.aspects.pointcut.expression.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;
import chameleon.util.Util;

public class IfPointcutExpression<E extends IfPointcutExpression<E>> extends RuntimePointcutExpression<E> {
	
	private SingleAssociation<IfPointcutExpression<E>, Expression> _expression = new SingleAssociation<IfPointcutExpression<E>, Expression>(this);

	public IfPointcutExpression(Expression expression) {
		setExpression(expression);
	}

	private void setExpression(Expression expression) {
		setAsParent(_expression, expression);
	}

	public Expression expression() {
		return _expression.getOtherEnd();
	}
	
	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(expression());
	}

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		return new MatchResult(this, joinpoint);
	}

	@Override
	public E clone() {
		Expression clonedExpression = null;
		if (expression() != null)
			clonedExpression = expression().clone();
		
		return (E) new IfPointcutExpression<E>(clonedExpression);
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
		Set<Class> resultList = new HashSet<Class>();
		
		resultList.add(Element.class);
		
		return resultList;
	}

}

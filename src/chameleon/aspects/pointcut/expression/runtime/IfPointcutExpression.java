package chameleon.aspects.pointcut.expression.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.language.ObjectOrientedLanguage;
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
	public E clone() {
		Expression clonedExpression = null;
		if (expression() != null)
			clonedExpression = expression().clone();
		
		return (E) new IfPointcutExpression<E>(clonedExpression);
	}

	@Override
	public Set<Class> supportedJoinpoints() {
		Set<Class> resultList = new HashSet<Class>();
		
		resultList.add(Element.class);
		
		return resultList;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = super.verifySelf();
		
		try {
			if (!expression().getType().sameAs(language(ObjectOrientedLanguage.class).booleanType()))
				result = result.and(new BasicProblem(this, "An if-expression may only contain boolean expressions"));
		} catch (LookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}

	
}

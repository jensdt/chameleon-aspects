package chameleon.aspects.pointcut.expression.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.element.Element;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.VerificationResult;


public abstract class PointcutExpressionDual<E extends PointcutExpressionDual<E>> extends PointcutExpression<E> {
	
	private SingleAssociation<PointcutExpressionDual, PointcutExpression> _expression1 = new SingleAssociation<PointcutExpressionDual, PointcutExpression>(this);
	private SingleAssociation<PointcutExpressionDual, PointcutExpression> _expression2 = new SingleAssociation<PointcutExpressionDual, PointcutExpression>(this);

	public PointcutExpressionDual(PointcutExpression expression1, PointcutExpression expression2) {
		super();
		setExpression1(expression1);
		setExpression2(expression2);
	}

	public PointcutExpression expression1() {
		return _expression1.getOtherEnd();
	}

	public PointcutExpression expression2() {
		return _expression2.getOtherEnd();
	}
	
	private void setExpression1(PointcutExpression expression1) {
		setAsParent(_expression1, expression1);
	}

	private void setExpression2(PointcutExpression expression2) {
		setAsParent(_expression2, expression2);
	}

	@Override
	public List<? extends Element> children() {
		List<PointcutExpression> children = new ArrayList<PointcutExpression>();
		
		if (expression1() != null)
			children.add(expression1());
		
		if (expression2() != null)
			children.add(expression2());
		
		return children;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = super.verifySelf();
		
		if (expression1() == null)
			result.and(new BasicProblem(this, "The first expression of this dual expression cannot be null."));
		
		if (expression2() == null)
			result.and(new BasicProblem(this, "The second expression of this dual expression cannot be null."));
		
		return result;
	}
	
	public List<? extends PointcutExpression<?>> asList() {
		List<PointcutExpression<?>> result = new ArrayList<PointcutExpression<?>>();
		result.addAll(expression1().asList());
		result.addAll(expression2().asList());
		
		return result;
	}
}

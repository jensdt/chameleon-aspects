package chameleon.aspects.pointcut.expression.generic;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.VerificationResult;


public abstract class PointcutExpressionSingle<E extends PointcutExpressionSingle<E>> extends AbstractStaticPointcutExpression<E> {
	private SingleAssociation<PointcutExpressionSingle, PointcutExpression> _expression = new SingleAssociation<PointcutExpressionSingle, PointcutExpression>(this);;

	public PointcutExpressionSingle(PointcutExpression expression) {
		super();
		setExpression(expression);
	}

	public PointcutExpression<?> expression() {
		return _expression.getOtherEnd();
	}

	private void setExpression(PointcutExpression expression) {
		setAsParent(_expression, expression);
	}
	
	@Override
	public List<? extends Element> children() {
		List<PointcutExpression> children = new ArrayList<PointcutExpression>();
		
		if (expression() != null)
			children.add(expression());
		
		return children;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = super.verifySelf();
		
		if (expression() == null)
			result.and(new BasicProblem(this, "The expression of this single expression cannot be null."));
		
		return result;
	}
	
	@Override
	public List<PointcutExpression<?>> toPostorderList() {
		List<PointcutExpression<?>> result = new ArrayList<PointcutExpression<?>>();
		
		result.addAll(expression().toPostorderList());
		result.add(this);
		
		return result;
	}
}

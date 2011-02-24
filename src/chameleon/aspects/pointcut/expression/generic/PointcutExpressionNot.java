package chameleon.aspects.pointcut.expression.generic;

import java.util.List;

import javax.management.RuntimeErrorException;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public class PointcutExpressionNot<E extends PointcutExpressionNot<E, T>, T extends Element> extends PointcutExpressionSingle<E, T> {

	public PointcutExpressionNot(PointcutExpression expression) {
		super(expression);
	}

	@Override
	public MatchResult matches(T joinpoint) throws LookupException {
		throw new RuntimeException();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionNot<E, T>(expression().clone());
	}
	
	/**
	 * 	{@inheritDoc}
	 * 
	 */
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return expression().hasParameter(fp);
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public int indexOfParameter(FormalParameter fp) {
		return expression().indexOfParameter(fp);
	}

}

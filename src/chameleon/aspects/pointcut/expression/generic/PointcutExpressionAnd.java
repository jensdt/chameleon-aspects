package chameleon.aspects.pointcut.expression.generic;

import java.util.HashSet;
import java.util.Set;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public class PointcutExpressionAnd<E extends PointcutExpressionAnd<E, T>, T extends Element> extends PointcutExpressionDual<E, T> {

	public PointcutExpressionAnd(PointcutExpression expression1, PointcutExpression expression2) {
		super(expression1, expression2);
	}

	@Override
	public MatchResult matches(T joinpoint) throws LookupException {
		MatchResult r1 = expression1().matches(joinpoint);
		MatchResult r2 = expression2().matches(joinpoint);
		
		if (r1.isMatch() && r2.isMatch())
			return new MatchResult(this, joinpoint);
		else
			return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionAnd<E, T>(expression1().clone(), expression2().clone());
	}

	/**
	 * 	{@inheritDoc}
	 * 
	 * 	Note that, due to class hierarchy, this isn't a simple intersection. Suppose the following class hierarchy:
	 * 
	 * 	A <- B
	 * 
	 * 	Consider the following cases:
	 * 	Expr1: {A}
	 * 	Expr2: {A}
	 * 
	 * 	=> Result {A} (note: not {A, A})
	 * 
	 * 	Expr1: {B}
	 * 	Expr2: {A}
	 * 
	 * 	=> Result {B}
	 * 
	 * 	Expr1: {A}
	 * 	Expr2: {B}
	 * 
	 *  => Result {B}
	 */
	@Override
	public Set<Class> supportedJoinpoints() {
		Set<Class> supported1 = expression1().supportedJoinpoints();
		
		Set<Class> supportedJoinpoints = new HashSet<Class>();
		
		for (Class c : supported1)
			if (expression2().isSupported(c))
				supportedJoinpoints.add(c);
		
		Set<Class> supported2 = expression1().supportedJoinpoints();
		
		for (Class c : supported2)
			if (!supportedJoinpoints.contains(c) && expression1().isSupported(c))
				supportedJoinpoints.add(c);
		
		return supportedJoinpoints;
	}
}

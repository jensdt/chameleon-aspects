package chameleon.aspects.pointcut;

import chameleon.aspects.pointcut.expression.CrossReferencePointcutExpression;
import chameleon.core.reference.CrossReference;

public class MatchResult<T extends CrossReferencePointcutExpression, U extends CrossReference> {
	private boolean match;
	private U joinpoint;
	private T expression;
	
	public MatchResult(T expression, U joinpoint) {
		this(true, expression, joinpoint);
	}
	
	public MatchResult(boolean match, T expression, U joinpoint) {
		setExpression(expression);
		setJoinpoint(joinpoint);
		setMatch(match);
	}
	
	public static <T extends CrossReferencePointcutExpression, U extends CrossReference> MatchResult<T, U> noMatch() {
		return new MatchResult<T, U>(false, null, null);
	}

	public U getJoinpoint() {
		return joinpoint;
	}

	public T getExpression() {
		return expression;
	}

	private void setJoinpoint(U joinpoint) {
		this.joinpoint = joinpoint;
	}

	private void setExpression(T expression) {
		this.expression = expression;
	}
	
	public boolean isMatch() {
		return match;
	}

	private void setMatch(boolean match) {
		this.match = match;
	}
}

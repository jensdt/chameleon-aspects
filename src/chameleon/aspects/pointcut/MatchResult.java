package chameleon.aspects.pointcut;

import chameleon.aspects.pointcut.expression.CrossReferencePointcutExpression;

public class MatchResult<T extends CrossReferencePointcutExpression> {
	private boolean match;
	private T element;
	
	public MatchResult(boolean match, T element) {
		setMatch(match);
		setElement(element);
	}
	
	public MatchResult(T element) {
		this(true, element);
	}
	

	private static MatchResult noMatch = new MatchResult(false, null);
	public static MatchResult noMatch() {
		return noMatch;
	}
	
	private void setMatch(boolean match) {
		this.match = match;
	}
	private void setElement(T element) {
		this.element = element;
	}
	public boolean isMatch() {
		return match;
	}
	public T getElement() {
		return element;
	}
	
	
}

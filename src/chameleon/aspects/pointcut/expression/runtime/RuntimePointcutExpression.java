package chameleon.aspects.pointcut.expression.runtime;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public abstract class RuntimePointcutExpression<E extends RuntimePointcutExpression<E>> extends PointcutExpression<E, Element> {
	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		return new MatchResult(this, joinpoint);
	}
	
	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		return new MatchResult(this, joinpoint);
	}
}

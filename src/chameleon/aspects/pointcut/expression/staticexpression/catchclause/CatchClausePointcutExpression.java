package chameleon.aspects.pointcut.expression.staticexpression.catchclause;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Statement;
import chameleon.support.statement.CatchClause;

public abstract class CatchClausePointcutExpression<E extends CatchClausePointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {

	@Override
	public MatchResult matches(Element joinpoint) throws LookupException {
		if (!(joinpoint.parent() instanceof CatchClause))
			return MatchResult.noMatch();
		
		if (((CatchClause) joinpoint.parent()).statement() != joinpoint)
			return MatchResult.noMatch();
		
		return new MatchResult<Element>(this, joinpoint);
	}
	
	@Override
	public boolean isSupported(Class<? extends Element> c) {
		if (Statement.class.isAssignableFrom(c))
			return true;
		
		return false;
	}
}
package chameleon.aspects.pointcut.expression.catchclause;

import java.util.Collections;
import java.util.List;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.EmptyStatement;

public class EmptyCatchClausePointcutExpression<E extends EmptyCatchClausePointcutExpression<E>> extends CatchClausePointcutExpression<E, CatchClause> {

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public MatchResult matches(CatchClause joinpoint) throws LookupException {
		// Note: when parsing Java, the 'statement' of a catch clause is *always* a block (see the parser)
		// This is a bit more general
		Statement st = joinpoint.statement();
		
		if (st instanceof EmptyStatement)
			return new MatchResult<EmptyCatchClausePointcutExpression, CatchClause>(this, joinpoint);
		
		if (st instanceof Block && ((Block) st).statements().isEmpty())
			return new MatchResult<EmptyCatchClausePointcutExpression, CatchClause>(this, joinpoint);
		
		//return joinpoint.statement();
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		return (E) new EmptyCatchClausePointcutExpression<E>();
	}

	@Override
	public MatchResult matchesInverse(CatchClause joinpoint) throws LookupException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}
}
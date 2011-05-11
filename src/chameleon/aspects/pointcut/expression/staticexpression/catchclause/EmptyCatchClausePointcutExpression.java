package chameleon.aspects.pointcut.expression.staticexpression.catchclause;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.support.statement.EmptyStatement;

public class EmptyCatchClausePointcutExpression<E extends EmptyCatchClausePointcutExpression<E>> extends CatchClausePointcutExpression<E> {

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public MatchResult matches(Element element) throws LookupException {
		if (!super.matches(element).isMatch())
			return MatchResult.noMatch();
		
		Statement joinpoint = (Statement) element;
		
		if (joinpoint instanceof EmptyStatement)
			return new MatchResult(this, joinpoint);
		
		if (element instanceof Block && ((Block) joinpoint).statements().isEmpty())
			return new MatchResult(this, joinpoint);
		
		return MatchResult.noMatch();
	}

	@Override
	public E clone() {
		return (E) new EmptyCatchClausePointcutExpression<E>();
	}

	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not yet implemented");
	}
}
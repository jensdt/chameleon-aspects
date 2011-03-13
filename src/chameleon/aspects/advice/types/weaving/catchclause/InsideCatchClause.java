package chameleon.aspects.advice.types.weaving.catchclause;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.weaving.AdviceWeaveResultProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.support.statement.CatchClause;

public class InsideCatchClause implements AdviceWeaveResultProvider<CatchClause, CatchClause> {

	@Override
	public CatchClause getWeaveResult(Advice advice, MatchResult<? extends PointcutExpression, ? extends CatchClause> joinpoint) throws LookupException {
		CatchClause newCatchClause = joinpoint.getJoinpoint().clone();
		newCatchClause.setStatement((Block) advice.body().clone());
		
		return newCatchClause;
	}

}

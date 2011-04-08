package chameleon.aspects.advice.types.weaving;

import java.util.List;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
// FIXME: this is catch-clause specific, treat it as such (rename and move) --- Edit 23/03 jens: maybe not anymore, moved parameter injection to runtime
public class ReturnAdviceProvider implements AdviceWeaveResultProvider<Block, List<Statement>> {

	@Override
	public List<Statement> getWeaveResult(Advice advice, MatchResult<? extends PointcutExpression, ? extends Block> joinpoint) throws LookupException {
		return advice.body().statements();
	}
}

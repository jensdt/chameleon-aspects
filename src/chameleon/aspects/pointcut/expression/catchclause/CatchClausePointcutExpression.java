package chameleon.aspects.pointcut.expression.catchclause;

import java.util.HashSet;
import java.util.Set;

import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.variable.FormalParameter;
import chameleon.support.statement.CatchClause;

public abstract class CatchClausePointcutExpression<E extends CatchClausePointcutExpression<E, T>, T extends CatchClause> extends PointcutExpression<E, T> {
	@Override
	public boolean hasParameter(FormalParameter fp) {
		return false;
	}

	@Override
	public int indexOfParameter(FormalParameter fp) {
		return -1;
	}

	@Override
	public Set<Class> supportedJoinpoints() {
		Set<Class> result = new HashSet<Class>();
		result.add(CatchClause.class);
		return result;
	}
}
package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.TypePointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.support.expression.InstanceofExpression;

public class RuntimeTypeCheck implements RuntimeExpressionProvider  {

	private NamedTargetExpression thisReference;
	
	public RuntimeTypeCheck(NamedTargetExpression thisReference) {
		this.thisReference = thisReference;
	}

	@Override
	public Expression<?> getExpression(RuntimePointcutExpression<?> expr) {
		if (!(expr instanceof TypePointcutExpression))
			return null;
		
		TypePointcutExpression<?> thisType = (TypePointcutExpression<?>) expr;
		
		return new InstanceofExpression(thisReference.clone(), thisType.getType());
	}
}
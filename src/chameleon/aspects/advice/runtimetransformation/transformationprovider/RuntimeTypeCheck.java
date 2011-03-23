package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import jnome.core.expression.ClassLiteral;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.TypePointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.support.member.simplename.method.RegularMethodInvocation;

public class RuntimeTypeCheck implements RuntimeExpressionProvider  {

	private Expression reference;
	
	public RuntimeTypeCheck(Expression reference) {
		this.reference = reference;
	}
	
	protected Expression getReference() {
		return reference;
	}

	@Override
	public Expression<?> getExpression(RuntimePointcutExpression<?> expr) {
		if (!(expr instanceof TypePointcutExpression))
			return null;
		
		TypePointcutExpression<?> thisType = (TypePointcutExpression<?>) expr;
		
		// Declared must be a super type of the given
		ClassLiteral getDeclaredClass = new ClassLiteral(thisType.getType());
		RegularMethodInvocation getGivenClass = new RegularMethodInvocation("getClass", getReference());
		
		RegularMethodInvocation test = new RegularMethodInvocation("isAssignableFrom", getDeclaredClass);
		test.addArgument(getGivenClass);
		
		return test;
	}
}
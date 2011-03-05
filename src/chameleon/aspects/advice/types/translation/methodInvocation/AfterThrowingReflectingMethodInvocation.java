package chameleon.aspects.advice.types.translation.methodInvocation;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.AfterThrowing;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;

public class AfterThrowingReflectingMethodInvocation extends ReflectiveMethodInvocation implements AfterThrowing {

	public AfterThrowingReflectingMethodInvocation(Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super("after-throwing", advice, joinpoint);
	}

	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();

		/*
		 *	Create the proceed call
		 */
		RegularMethodInvocation proceedInvocation = createProceedInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(methodNameParamName), new NamedTargetExpression(argumentNameParamName));

		/*
		 * 	Add the return statement
		 */
		adviceBody.addStatement(new ReturnStatement(proceedInvocation));
		
		return adviceBody;
	}
	
	@Override
	protected Block getRethrowBody(NamedTargetExpression target) {
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(target);

		rethrowBody.addBlock((Block) advice().body());
		rethrowBody.addStatement(rethrow);
		
		return rethrowBody;
	}
	
}

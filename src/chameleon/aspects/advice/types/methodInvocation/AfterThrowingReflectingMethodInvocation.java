package chameleon.aspects.advice.types.methodInvocation;

import java.util.Map;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.AfterThrowing;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;

public class AfterThrowingReflectingMethodInvocation extends ReflectiveMethodInvocation implements AfterThrowing<Block, MethodInvocation> {

	public AfterThrowingReflectingMethodInvocation(Advice advice, Map<String, String> variableNames) {
		super("after-throwing", advice, variableNames);
	}

	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();
		
		String objectParamName = getVariableNames().get("objectParamName");
		String methodNameParamName = getVariableNames().get("methodNameParamName");
		String argumentNameParamName = getVariableNames().get("argumentNameParamName");
		
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

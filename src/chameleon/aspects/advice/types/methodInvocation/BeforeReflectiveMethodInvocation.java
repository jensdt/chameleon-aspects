package chameleon.aspects.advice.types.methodInvocation;

import java.util.Map;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.Before;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;

public class BeforeReflectiveMethodInvocation extends ReflectiveMethodInvocation implements Before<Block, MethodInvocation> {

	public BeforeReflectiveMethodInvocation(Advice advice, Map<String, String> variableNames) {
		super("before", advice, variableNames);
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
		 *	Add the advice-body itself 
		 */
		adviceBody.addBlock(((Block) advice().body()).clone());
		
		/*
		 * 	Add the return statement
		 */
		adviceBody.addStatement(new ReturnStatement(proceedInvocation));
		
		return adviceBody;
	}
}

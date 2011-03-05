package chameleon.aspects.advice.types.translation.methodInvocation;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.Before;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;

public class BeforeReflectiveMethodInvocation extends ReflectiveMethodInvocation implements Before {

	public BeforeReflectiveMethodInvocation(Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super("before", advice, joinpoint);
	}
	
	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();
		
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

package chameleon.aspects.advice.types.translation.reflection.methodInvocation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;

public class BeforeReflectiveMethodInvocation extends ReflectiveMethodInvocation  {

	public BeforeReflectiveMethodInvocation(MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super(joinpoint);
	}

	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();
		
		/*
		 *	Add the advice-body itself 
		 */
		adviceBody.addBlock(((Block) advice().body()).clone());
		
		/*
		 * 	Add the return statement
		 */
		RegularMethodInvocation proceedInvocation = createProceedInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(methodNameParamName), new NamedTargetExpression(argumentNameParamName));
		adviceBody.addStatement(new ReturnStatement(proceedInvocation));
		
		return adviceBody;
	}
}

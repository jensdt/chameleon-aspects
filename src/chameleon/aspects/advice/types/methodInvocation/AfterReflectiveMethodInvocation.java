package chameleon.aspects.advice.types.methodInvocation;

import java.util.Map;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.After;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.FinallyClause;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.TryStatement;

public class AfterReflectiveMethodInvocation extends ReflectiveMethodInvocation implements After<Block, MethodInvocation> {

	public AfterReflectiveMethodInvocation(Advice advice, Map<String, String> variableNames) {
		super("after", advice, variableNames);
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
	protected TryStatement getEnclosingTry(Block tryBody, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) throws LookupException {
		// TODO Auto-generated method stub
		TryStatement enclosingTry = super.getEnclosingTry(tryBody, joinpoint);
		enclosingTry.setFinallyClause(new FinallyClause(((Block) advice().body()).clone()));
		
		return enclosingTry;
	}
}

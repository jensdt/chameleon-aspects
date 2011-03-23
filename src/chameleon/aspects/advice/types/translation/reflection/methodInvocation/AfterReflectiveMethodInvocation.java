package chameleon.aspects.advice.types.translation.reflection.methodInvocation;

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

public class AfterReflectiveMethodInvocation extends ReflectiveMethodInvocation {

	public AfterReflectiveMethodInvocation(MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super(joinpoint);
		
	}
	
	@Override
	public TryStatement getEnclosingTry(Block tryBody) throws LookupException {
		TryStatement enclosingTry = super.getEnclosingTry(tryBody);
		enclosingTry.setFinallyClause(new FinallyClause(((Block) advice().body()).clone()));
		
		return enclosingTry;
	}

	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();

		/*
		 * 	Add the return statement
		 */
		RegularMethodInvocation proceedInvocation = createProceedInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(methodNameParamName), new NamedTargetExpression(argumentNameParamName));
		adviceBody.addStatement(new ReturnStatement(proceedInvocation));
		
		return adviceBody;
	}
}
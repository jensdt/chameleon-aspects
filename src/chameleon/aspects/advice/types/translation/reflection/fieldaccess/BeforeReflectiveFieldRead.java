package chameleon.aspects.advice.types.translation.reflection.fieldaccess;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;

public class BeforeReflectiveFieldRead extends ReflectiveFieldRead {

	public BeforeReflectiveFieldRead(MatchResult<?, ?> joinpoint) {
		super(joinpoint);
	}
	
		
	protected Block getBody() {
		Block adviceBody = new Block();

		/*
		 *	Add the advice-body itself 
		 */
		adviceBody.addBlock(((Block) advice().body()).clone());
		
		/*
		 *	Create the proceed call
		 */
		RegularMethodInvocation<?> getValueInvocation = createGetFieldValueInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(fieldName));			
		
		/*
		 * 	Add the return statement
		 */
		adviceBody.addStatement(new ReturnStatement(getValueInvocation));
		
		return adviceBody;
	}
}

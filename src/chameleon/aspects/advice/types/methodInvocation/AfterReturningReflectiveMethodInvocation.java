package chameleon.aspects.advice.types.methodInvocation;

import java.util.Map;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.AfterReturning;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public class AfterReturningReflectiveMethodInvocation extends ReflectiveMethodInvocation implements AfterReturning<Block, MethodInvocation> {

	public AfterReturningReflectiveMethodInvocation(Advice advice, Map<String, String> variableNames) {
		super("after-returning", advice, variableNames);
	}

	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();
		
		String objectParamName = getVariableNames().get("objectParamName");
		String methodNameParamName = getVariableNames().get("methodNameParamName");
		String argumentNameParamName = getVariableNames().get("argumentNameParamName");
		String retvalName = getVariableNames().get("retvalName");
		
		/*
		 *	Create the proceed call
		 */
		RegularMethodInvocation proceedInvocation = createProceedInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(methodNameParamName), new NamedTargetExpression(argumentNameParamName));

		/*
		 *	Add the proceed-invocation, assign it to a local variable 
		 */
		LocalVariableDeclarator returnVal = new LocalVariableDeclarator(new BasicTypeReference("T"));
		
		VariableDeclaration returnValDecl = new VariableDeclaration(retvalName);
		returnValDecl.setInitialization(proceedInvocation);
		returnVal.add(returnValDecl);
	
		adviceBody.addStatement(returnVal);
		
		/*
		 *	Add the advice-body itself 
		 */
		adviceBody.addBlock(((Block) advice().body()).clone());
		
		/*
		 * 	Add the return statement
		 */
		adviceBody.addStatement(new ReturnStatement(new NamedTargetExpression(retvalName)));
		
		return adviceBody;
	}

}
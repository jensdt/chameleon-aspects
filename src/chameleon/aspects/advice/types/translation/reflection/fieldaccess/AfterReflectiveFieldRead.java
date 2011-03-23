package chameleon.aspects.advice.types.translation.reflection.fieldaccess;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.variable.LocalVariable;
import chameleon.support.variable.LocalVariableDeclarator;

public class AfterReflectiveFieldRead extends ReflectiveFieldRead {

	public AfterReflectiveFieldRead(MatchResult<?, ?> joinpoint) {
		super(joinpoint);
	}
			
	protected Block getBody() {
		Block adviceBody = new Block();

		/*
		 *	Create the proceed call
		 */
		RegularMethodInvocation<?> getValueInvocation = createGetFieldValueInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(fieldName));

		/*
		 *	Add the proceed-invocation, assign it to a local variable 
		 */
		LocalVariableDeclarator returnVal = new LocalVariableDeclarator(new BasicTypeReference<BasicTypeReference<?>>("T"));
		
		VariableDeclaration<LocalVariable> returnValDecl = new VariableDeclaration<LocalVariable>(retvalName);
		returnValDecl.setInitialization(getValueInvocation);
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
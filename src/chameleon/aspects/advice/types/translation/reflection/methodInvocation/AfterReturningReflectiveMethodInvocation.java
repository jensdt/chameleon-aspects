package chameleon.aspects.advice.types.translation.reflection.methodInvocation;

import chameleon.aspects.advice.types.Returning;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.variable.VariableDeclaration;
import chameleon.exception.ModelException;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public class AfterReturningReflectiveMethodInvocation extends ReflectiveMethodInvocation {

	public AfterReturningReflectiveMethodInvocation(MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super(joinpoint);
	}

	@Override
	protected Block getInnerBody() {
		Block adviceBody = new Block();

		RegularMethodInvocation proceedInvocation = createProceedInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(methodNameParamName), new NamedTargetExpression(argumentNameParamName));
		
		/*
		 *	Add the proceed-invocation, assign it to a local variable 
		 */
		LocalVariableDeclarator returnVal = new LocalVariableDeclarator(new BasicTypeReference("T"));
		
		/*
		 *	Find the name of the local variable to assign the value to 	
		 */
		String returnName = retvalName;
		try {
			Returning m = (Returning) advice().modifiers(advice().language().property("advicetype.returning")).get(0);
			if (m.hasReturnParameter())
				returnName = m.returnParameter().getName();
		} catch (ModelException e) {
			
		}
		
		
		VariableDeclaration returnValDecl = new VariableDeclaration(returnName);
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
		adviceBody.addStatement(new ReturnStatement(new NamedTargetExpression(returnName)));
		
		return adviceBody;
	}
}
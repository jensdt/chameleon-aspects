package chameleon.aspects.advice.types.translation.reflection.methodInvocation;

import chameleon.aspects.advice.types.Throwing;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableDeclaration;
import chameleon.exception.ModelException;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.variable.LocalVariable;
import chameleon.support.variable.LocalVariableDeclarator;

public class AfterThrowingReflectiveMethodInvocation extends ReflectiveMethodInvocation  {

	public AfterThrowingReflectiveMethodInvocation(MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super(joinpoint);
		
	}
	
	@Override
	public CatchClause getCatchClause(String name, Type caughtType) {
		Block body = new Block();
		ThrowStatement rethrow = new ThrowStatement(new NamedTargetExpression(name));
		
		try {
			Throwing m = (Throwing) advice().modifiers(advice().language().property("advicetype.throwing")).get(0);
			Type declaredType = m.exceptionParameter().getType();
			// Do a type check if there is a type defined
			if (m.hasExceptionParameter()) {
				// If the declared type is the same or a super type of the type caught, add the advice and expose the parameter
				if (caughtType.assignableTo(declaredType)) {
					LocalVariableDeclarator paramExpose = new LocalVariableDeclarator(m.exceptionParameter().getTypeReference().clone());
					paramExpose.add(new VariableDeclaration<LocalVariable>(m.exceptionParameter().getName(), new NamedTargetExpression(name)));
					body.addStatement(paramExpose);
					body.addBlock((Block) advice().body());
					body.addStatement(rethrow);
				}
				// If the declared type is a subtype of the type caught, we need a runtime check
				else if (declaredType.subTypeOf(caughtType)) {
					Block innerBody = new Block();
					LocalVariableDeclarator paramExpose = new LocalVariableDeclarator(m.exceptionParameter().getTypeReference().clone());
					paramExpose.add(new VariableDeclaration<LocalVariable>(m.exceptionParameter().getName(), new ClassCastExpression(m.exceptionParameter().getTypeReference().clone(), new NamedTargetExpression(name))));
					
					innerBody.addStatement(paramExpose);
					innerBody.addBlock((Block) advice().body().clone());
					
					InstanceofExpression instanceOf = new InstanceofExpression(new NamedTargetExpression(name), new BasicTypeReference(declaredType.getFullyQualifiedName()));
					IfThenElseStatement runtimeTest = new IfThenElseStatement(instanceOf, innerBody, null);
					
					body.addStatement(runtimeTest);
					body.addStatement(rethrow);
				}
				else {
					// The types aren't related, so just re throw the exception
					body.addStatement(rethrow);
				}
			} else {
				body.addBlock((Block) advice().body());
				body.addStatement(rethrow);
			}
				
		} catch (ModelException e) {
			// This shouldn't be able to occur
			e.printStackTrace();
		}
		
		return new CatchClause(new FormalParameter(name, new BasicTypeReference(caughtType.getFullyQualifiedName())), body);
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
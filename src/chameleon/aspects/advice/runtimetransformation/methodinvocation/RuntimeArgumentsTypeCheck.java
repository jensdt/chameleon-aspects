package chameleon.aspects.advice.runtimetransformation.methodinvocation;

import java.util.List;

import jnome.core.expression.ArrayAccessExpression;
import chameleon.aspects.advice.runtimetransformation.RuntimeTransformer;
import chameleon.aspects.advice.types.translation.methodInvocation.ReflectiveMethodInvocation;
import chameleon.aspects.pointcut.expression.runtime.ArgsPointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.TypeOrParameter;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.RegularImplementation;
import chameleon.core.method.exception.TypeExceptionDeclaration;
import chameleon.core.statement.Block;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.FilledArrayIndex;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;

public class RuntimeArgumentsTypeCheck extends RuntimeTransformer<NormalMethod> {
	
	private ReflectiveMethodInvocation reflectiveMethodInvocation;
	
	public RuntimeArgumentsTypeCheck(ReflectiveMethodInvocation reflectiveMethodInvocation) {
		this.reflectiveMethodInvocation = reflectiveMethodInvocation;
	}
	
	@Override
	public void transform(NormalMethod method, RuntimePointcutExpression expr) {
		if (method == null || !(expr instanceof ArgsPointcutExpression))
			return;
		
		// Transform the method body to add the runtime check
		Block runtimeCheck = new Block();
		
		ArgsPointcutExpression argumentsExpression = (ArgsPointcutExpression) expr;
		
		// First, add a check if the number of parameters matches
		NamedTargetExpression parLength = new NamedTargetExpression("length", new NamedTargetExpression(reflectiveMethodInvocation.argumentNameParamName));
		InfixOperatorInvocation equals = new InfixOperatorInvocation("!=", parLength);
		equals.addArgument(new RegularLiteral(new BasicTypeReference("int"), Integer.toString(argumentsExpression.parameters().size())));
		
		Block ifBlock = new Block();
		ifBlock.addStatement(new ReturnStatement(getProceedInvocation()));
		IfThenElseStatement ite = new IfThenElseStatement(equals, ifBlock, null);
		
		runtimeCheck.addStatement(ite);
		// Add a check for each parameter defined in the Arguments expression
		int i = 0;
		for (NamedTargetExpression parameter : (List<NamedTargetExpression>) argumentsExpression.parameters()) {
			try {
				TypeReference t = new BasicTypeReference(parameter.getType().getFullyQualifiedName());
				// Access the correct element of the array
				ArrayAccessExpression arrayAccess = new ArrayAccessExpression(new NamedTargetExpression(reflectiveMethodInvocation.argumentNameParamName));
				arrayAccess.addIndex(new FilledArrayIndex(new RegularLiteral(new BasicTypeReference("int"), Integer.toString(i++))));
							
				// Create the instanceof
				InstanceofExpression test = new InstanceofExpression(arrayAccess, t.clone());
				
				// Negate it
				PrefixOperatorInvocation negation = new PrefixOperatorInvocation("!", test);
				
				ifBlock = new Block();
				ifBlock.addStatement(new ReturnStatement(getProceedInvocation()));
				ite = new IfThenElseStatement(negation, ifBlock, null);
				
				runtimeCheck.addStatement(ite);
			} catch (LookupException e) {
				
			}
		}
			
		// Re-throw unchecked exceptions (subclasses of RuntimeException )	
		TryStatement exceptionHandler = null;
		try {
			exceptionHandler = new TryStatement(runtimeCheck);
			exceptionHandler.addCatchClause(getRethrow("unchecked", new BasicTypeReference("RuntimeException")));
	
			// Add a re-throw for each checked exception
			int exceptionIndex = 0;
			List<TypeExceptionDeclaration> checkedTypeExceptions = reflectiveMethodInvocation.getCheckedExceptionsWithoutSubtypes(reflectiveMethodInvocation.getJoinpoint().getJoinpoint().getElement());
			
			for (TypeExceptionDeclaration exception : checkedTypeExceptions) {
				exceptionHandler.addCatchClause(getRethrow("ex" + exceptionIndex, exception.getTypeReference().clone()));
				
				exceptionIndex++;
			}
			
			// Add a catch all. This isn't actually necessary since we already handled all cases, but since the generic proceed method throws a throwable we need it to prevent compile errors
			exceptionHandler.addCatchClause(reflectiveMethodInvocation.getCatchAll());
		} catch (LookupException e){
			// Will only occur with a bug, not in normal usage
			System.out.println("Creating surrounding try in runtime check threw LookupEx");
			e.printStackTrace();
			
		}
		
		Block finalBody = new Block();
		finalBody.addStatement(exceptionHandler);
		finalBody.addBlock(method.body());
		
		method.setImplementation(new RegularImplementation(finalBody));
	}
	
	private CatchClause getRethrow(String name, TypeReference type) {
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(new NamedTargetExpression(name));

		rethrowBody.addStatement(rethrow);
		
		return new CatchClause(new FormalParameter(name, type), rethrowBody);
	}
	
	
	private RegularMethodInvocation getProceedInvocation() {
		try {
			return reflectiveMethodInvocation.createProceedInvocation(new NamedTarget(reflectiveMethodInvocation.advice().aspect().name()), new NamedTargetExpression(reflectiveMethodInvocation.objectParamName), new NamedTargetExpression(reflectiveMethodInvocation.methodNameParamName), new NamedTargetExpression(reflectiveMethodInvocation.argumentNameParamName));
		} catch (LookupException e) {
			System.out.println("creating proceed invocation threw lookupEx");
			e.printStackTrace();
			return null;
		}
	}

}

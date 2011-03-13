package chameleon.aspects.advice.runtimetransformation.methodinvocation;

import java.util.List;

import chameleon.aspects.advice.runtimetransformation.RuntimeTransformer;
import chameleon.aspects.advice.types.translation.methodInvocation.ReflectiveMethodInvocation;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.TypePointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.RegularImplementation;
import chameleon.core.method.exception.TypeExceptionDeclaration;
import chameleon.core.statement.Block;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;

public class RuntimeTypeCheck extends RuntimeTransformer<NormalMethod>  {

	private ReflectiveMethodInvocation reflectiveMethodInvocation;
	private NamedTargetExpression thisReference;
	
	public RuntimeTypeCheck(ReflectiveMethodInvocation reflectiveMethodInvocation, NamedTargetExpression thisReference) {
		this.reflectiveMethodInvocation = reflectiveMethodInvocation;
		this.thisReference = thisReference;
	}

	@Override
	public void transform(NormalMethod method, RuntimePointcutExpression expr) {
		if (method == null || !(expr instanceof TypePointcutExpression))
			return;
		
		TypePointcutExpression thisType = (TypePointcutExpression) expr;
		
		// Transform the method body to add the runtime check
		Block runtimeCheck = new Block();
		
		// Add the runtime check
		InstanceofExpression instanceOf = new InstanceofExpression(thisReference.clone(), thisType.getType());
		
		PrefixOperatorInvocation negation = new PrefixOperatorInvocation("!", instanceOf);
		
		Block ifBlock = new Block();
		ifBlock.addStatement(new ReturnStatement(getProceedInvocation()));
		IfThenElseStatement ite = new IfThenElseStatement(negation, ifBlock, null);
		
		runtimeCheck.addStatement(ite);
		
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
	
	// FIXME: refactor
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

	@Override
	public Expression getExpression(RuntimePointcutExpression expr) {
		// TODO Auto-generated method stub
		return null;
	}

}

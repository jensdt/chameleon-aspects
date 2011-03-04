package chameleon.aspects.advice.types.methodInvocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jnome.core.expression.ArrayAccessExpression;
import jnome.core.expression.invocation.ConstructorInvocation;
import jnome.core.type.BasicJavaTypeReference;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.AdviceTypeImpl;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.method.exception.ExceptionDeclaration;
import chameleon.core.method.exception.TypeExceptionDeclaration;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.FilledArrayIndex;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.EmptyStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public abstract class ReflectiveMethodInvocation extends AdviceTypeImpl<Block, MethodInvocation> {

	public ReflectiveMethodInvocation(String name, Advice advice, Map<String, String> variableNames) {
		super(name, advice);
		this.variableNames = variableNames;
	}
	
	private Map<String, String> variableNames;
	
	protected Map<String, String> getVariableNames() {
		return variableNames;
	}
	
	protected abstract Block getInnerBody() throws LookupException;
	
	protected List<TypeExceptionDeclaration> getCheckedExceptionsWithoutSubtypes(Method method) throws LookupException {
		List<TypeExceptionDeclaration> checkedTypeExceptions = new ArrayList<TypeExceptionDeclaration>();
		
		// Copy all checked exceptions
		for (ExceptionDeclaration exception : method.getExceptionClause().exceptionDeclarations()) {
			if (exception instanceof TypeExceptionDeclaration && ((ObjectOrientedLanguage) method.nearestAncestor(CompilationUnit.class).language(ObjectOrientedLanguage.class)).isCheckedException(((TypeExceptionDeclaration) exception).getType()))
				checkedTypeExceptions.add((TypeExceptionDeclaration) exception);
		}
		
		// Now remove checked exceptions that are a sub type of another declared exception
		Iterator<TypeExceptionDeclaration> exceptionIterator = checkedTypeExceptions.iterator();
		
		while (exceptionIterator.hasNext()) {
			TypeExceptionDeclaration currentException = exceptionIterator.next();
			for (TypeExceptionDeclaration other : checkedTypeExceptions) {
				if (currentException != other && currentException.getType().assignableTo(other.getType())) {
					exceptionIterator.remove();
					break;
				}
			}
		}
		
		return checkedTypeExceptions;
	}
	
	protected Block getRethrowBody(NamedTargetExpression target) {
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(target);

		rethrowBody.addStatement(rethrow);
		
		return rethrowBody;
	}
	
	protected TryStatement getEnclosingTry(Block tryBody, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) throws LookupException {
		// Re-throw unchecked exceptions (subclasses of RuntimeException )	
		TryStatement exceptionHandler = new TryStatement(tryBody);
		exceptionHandler.addCatchClause(new CatchClause(new FormalParameter("unchecked", new BasicTypeReference("RuntimeException")), getRethrowBody(new NamedTargetExpression("unchecked"))));

		// Add a re-throw for each checked exception
		int exceptionIndex = 0;
		List<TypeExceptionDeclaration> checkedTypeExceptions = getCheckedExceptionsWithoutSubtypes(joinpoint.getJoinpoint().getElement());
		
		for (TypeExceptionDeclaration exception : checkedTypeExceptions) {
			exceptionHandler.addCatchClause(getRethrow("ex" + exceptionIndex, exception.getTypeReference().clone()));
			
			exceptionIndex++;
		}
		
		// Add a catch all. This isn't actually necessary since we already handled all cases, but since the generic proceed method throws a throwable we need it to prevent compile errors
		exceptionHandler.addCatchClause(getCatchAll());
		
		return exceptionHandler;
	}
	
	protected CatchClause getRethrow(String name, TypeReference type) {
		return new CatchClause(new FormalParameter(name, type), getRethrowBody(new NamedTargetExpression(name)));
	}
	
	protected CatchClause getCatchAll() {
		Block emptyCatchBody = new Block();
		emptyCatchBody.addStatement(new EmptyStatement());
		
		return new CatchClause(new FormalParameter("thrwbl", new BasicTypeReference("Throwable")), emptyCatchBody);
	}

	protected Statement getDieStatement() {
		ThrowStatement throwError = new ThrowStatement(new ConstructorInvocation(new BasicJavaTypeReference("Error"), null));
		return throwError;
	}

	@Override
	public Block getAdviceTransformationResult(CompilationUnit compilationUnit, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) throws LookupException {
		
		String argumentNameParamName = getVariableNames().get("argumentNameParamName");
		String argumentIndexParamName = getVariableNames().get("argumentIndexParamName");
		
		
		Block finalBody = new Block();
		/*
		 * 	Inject the parameters
		 */
		List<Statement> parameterDeclarations = getInjectedParameterDeclarations(new NamedTargetExpression(argumentNameParamName), new NamedTargetExpression(argumentIndexParamName));
		for (Statement parameterDeclaration : parameterDeclarations)
			finalBody.addStatement(parameterDeclaration);
		
		/*
		 *	Get the inner body 
		 */
		Block adviceBody = getInnerBody();
		
		/*
		 *	Check if we need to wrap in a try - catch block. Around advice without proceed calls is one instance where this doesn't have to be done
		 */
		if (encloseWithTry()) {
		
			/*
			 * 	Create the surrounding try-catch block for exception handling
			 */
			TryStatement exceptionHandler = getEnclosingTry(adviceBody, joinpoint);
		
			/*
			 * 	Complete the complete body
			 */
			finalBody.addStatement(exceptionHandler);
			
			
			// Add a 'die statement' - after the try {} catch, since the try-block should return anyway. 
			// If it doesn't, then an error occurred anyway.

			// If this happens, it is due to a bug in the weaver!
			finalBody.addStatement(getDieStatement());
		} else {
			finalBody = adviceBody;
		}
		
		return finalBody;
	}
	
	protected boolean encloseWithTry() {
		return true;
	}

	protected List<Statement> getInjectedParameterDeclarations(Expression argumentsTarget, Expression argumentsIndexTarget) {
		List<Statement> declarations = new ArrayList<Statement>();
		
		// Iterate over every formal parameter in the advice and add its declaration to the result list
		for (FormalParameter fp : (List<FormalParameter>) advice().formalParameters()) {
			int index = advice().pointcutReference().indexOfParameter(fp);
			
			LocalVariableDeclarator parameterInjector = new LocalVariableDeclarator(fp.getTypeReference().clone());
			VariableDeclaration parameterInjectorDecl = new VariableDeclaration(fp.getName());
			
			// Add the indirection to the correct parameter
			ArrayAccessExpression argumentsIndexAccess = new ArrayAccessExpression(argumentsTarget);
			argumentsIndexAccess.addIndex(new FilledArrayIndex(new RegularLiteral(new BasicJavaTypeReference("int"), Integer.toString(index))));

			ArrayAccessExpression argumentsAccess = new ArrayAccessExpression(argumentsIndexTarget);
			argumentsAccess.addIndex(new FilledArrayIndex(argumentsIndexAccess));

			// Add the cast, since the arguments is just an Object array
			ClassCastExpression cast = new ClassCastExpression(fp.getTypeReference().clone(), argumentsAccess);
			
			parameterInjectorDecl.setInitialization(cast);
			parameterInjector.add(parameterInjectorDecl);
			
			declarations.add(parameterInjector);
		}
		
		return declarations;
	}
	
	public RegularMethodInvocation createProceedInvocation(InvocationTarget aspectClassTarget, Expression objectTarget, Expression methodNameTarget, Expression argumentsTarget) {
		RegularMethodInvocation proceedInvocation = new RegularMethodInvocation("proceed", aspectClassTarget);
		proceedInvocation.addArgument((new BasicTypeArgument(new BasicTypeReference("T"))));
		
		proceedInvocation.addArgument(objectTarget);
		proceedInvocation.addArgument(methodNameTarget);
		proceedInvocation.addArgument(argumentsTarget);
		
		return proceedInvocation;
	}

}
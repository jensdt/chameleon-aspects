package chameleon.aspects.advice.types.translation.methodInvocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jnome.core.expression.ArrayAccessExpression;
import jnome.core.expression.ArrayCreationExpression;
import jnome.core.expression.invocation.ConstructorInvocation;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.variable.JavaVariableDeclaration;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.Aspect;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.AdviceTypeImpl;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.method.RegularImplementation;
import chameleon.core.method.exception.ExceptionDeclaration;
import chameleon.core.method.exception.TypeExceptionDeclaration;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.support.expression.AssignmentExpression;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.FilledArrayIndex;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
import chameleon.support.member.simplename.operator.postfix.PostfixOperatorInvocation;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.EmptyStatement;
import chameleon.support.statement.ForStatement;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.SimpleForControl;
import chameleon.support.statement.StatementExprList;
import chameleon.support.statement.StatementExpression;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public abstract class ReflectiveMethodInvocation extends AdviceTypeImpl {

	protected final String objectParamName = "_$object";
	protected final String methodNameParamName = "_$methodName";
	protected final String argumentNameParamName = "_$arguments";
	protected final String argumentIndexParamName = "_$argumentIndex";
	protected final String retvalName = "_$retval";
	
	private MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint;
	
	public ReflectiveMethodInvocation(String name, Advice advice, MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super(name, advice);
		this.joinpoint = joinpoint;
	}
	
	protected abstract Block getInnerBody() throws LookupException;
	
	/**
	 * 	Return the type used for implementing the advice - methods. If it doesn't exist, it is created. It is always created in the same
	 * 	compilation unit as the aspect.
	 * 
	 * 	@param 	compilationUnit
	 * 			The compilation unit the aspect belongs to
	 * 	@param 	name
	 * 			The name of the aspect
	 * 	@return	The type representing the aspect
	 */
	protected RegularType getOrCreateAspectClass(CompilationUnit compilationUnit, final String name) {
		// Find the aspect class
		List<RegularType> aspectClasses = compilationUnit.descendants(RegularType.class, new SafePredicate<RegularType>() {
			@Override
			public boolean eval(RegularType object) {
				return object.getName().equals(name);
			}
		});
		
		// Sanity check
		if (aspectClasses.size() > 1)
			throw new RuntimeException("More than one aspect class");
		
		// Create the aspect class, or get it if it already exists
		RegularType aspectClass;
		if (aspectClasses.isEmpty()) {
			// No aspect class yet
			aspectClass = new RegularType(name);
			addProceedMethod(aspectClass); 
			compilationUnit.namespacePart(1).add(aspectClass);
		} else {
			aspectClass = aspectClasses.get(0);
		}
		
		return aspectClass;
	}
	
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
	public void transform(CompilationUnit compilationUnit, Advice advice) throws LookupException {
		
		Aspect aspect = advice.aspect();
		
		// Get the class we are going to create this method in
		RegularType aspectClass = getOrCreateAspectClass(compilationUnit, aspect.name());
			
		// Get the naming registries
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		NamingRegistry<Method> methodNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("javamethod");			
		
		Method m = joinpoint.getJoinpoint().getElement();
		final String adviceMethodName = "advice_" + adviceNamingRegistry.getName(advice) + "_" + methodNamingRegistry.getName(m);
		
		// Check if this method has already been defined (which is possible, if a joinpoint to the same method is found multiple times)
		List<Method> definedMethods = compilationUnit.descendants(Method.class, new SafePredicate<Method>() {
			@Override
			public boolean eval(Method object) {
				return object.name().equals(adviceMethodName);
			}
		});
		
		if (!definedMethods.isEmpty())
			return;
		
		
		DeclarationWithParametersHeader header = new SimpleNameDeclarationWithParametersHeader(adviceMethodName);
		
		TypeReference returnType = new BasicTypeReference("T");			
		NormalMethod adviceMethod = new NormalMethod(header, returnType);
		
		adviceMethod.addModifier(new Public());
		adviceMethod.addModifier(new Static());
		
		header.addTypeParameter(new FormalTypeParameter(new SimpleNameSignature("T")));

		// Copy the exceptions
		adviceMethod.setExceptionClause(m.getExceptionClause().clone());

		// Add all the parameters to allow the reflective invocation 
		FormalParameter object = new FormalParameter(objectParamName, new BasicTypeReference("Object"));
		header.addFormalParameter(object);
		
		FormalParameter methodName = new FormalParameter(methodNameParamName, new BasicTypeReference("String"));
		header.addFormalParameter(methodName);
		
		FormalParameter methodArguments = new FormalParameter(argumentNameParamName, new ArrayTypeReference(new BasicJavaTypeReference("Object")));
		header.addFormalParameter(methodArguments);
		
		
		FormalParameter methodArgumentsIndex = new FormalParameter(argumentIndexParamName, new ArrayTypeReference(new BasicJavaTypeReference("int")));
		header.addFormalParameter(methodArgumentsIndex);
		
		// Get the body
		Block body = getBody(joinpoint);
		
		// Set the method body
		adviceMethod.setImplementation(new RegularImplementation(body));
		
		// Add the method
		aspectClass.add(adviceMethod);
	}
	
	protected Block getBody(MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) throws LookupException {
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

	
	protected void addProceedMethod(RegularType aspectClass) {
		
		
		
		/*
		 *	Create method header
		 */
		
		FormalParameter object = new FormalParameter(objectParamName, new BasicTypeReference("Object"));
		FormalParameter methodParam = new FormalParameter(methodNameParamName, new BasicTypeReference("String"));
		FormalParameter methodArguments = new FormalParameter(argumentNameParamName, new ArrayTypeReference(new BasicJavaTypeReference("Object")));
		
		DeclarationWithParametersHeader pHeader = new SimpleNameDeclarationWithParametersHeader("proceed");
		NormalMethod proceedMethod = new NormalMethod(pHeader, new BasicTypeReference("T"));
		
		pHeader.addFormalParameter(object);
		pHeader.addFormalParameter(methodParam);
		pHeader.addFormalParameter(methodArguments);

		
		pHeader.addTypeParameter(new FormalTypeParameter(new SimpleNameSignature("T")));
		
		proceedMethod.addModifier(new Private());
		proceedMethod.addModifier(new Static());

		//proceedMethod.getExceptionClause().add(new TypeExceptionDeclaration(new BasicJavaTypeReference("java.lang.reflect.InvocationTargetException")));
		proceedMethod.getExceptionClause().add(new TypeExceptionDeclaration(new BasicJavaTypeReference("Throwable")));
		
		/*
		 * 	Create method body
		 */
		Block proceedMethodBody = new Block();

		// Class invocationClass;
		LocalVariableDeclarator invocationClass = new LocalVariableDeclarator(new BasicJavaTypeReference("Class"));
		JavaVariableDeclaration invocationClassDecl = new JavaVariableDeclaration("invocationClass");
		
		invocationClass.add(invocationClassDecl);
		proceedMethodBody.addStatement(invocationClass);
		/* if (_$object instanceof Class) {
			invocationClass = (Class) _$object;
		}
		else {
			invocationClass = _$object.getClass();
		}
		*/
		InstanceofExpression testObject = new InstanceofExpression(new NamedTargetExpression(objectParamName), new BasicJavaTypeReference("Class"));
		ClassCastExpression objectCastToClass = new ClassCastExpression(new BasicJavaTypeReference("Class"), new NamedTargetExpression(objectParamName));
		AssignmentExpression assignObjIf = new AssignmentExpression(new NamedTargetExpression("invocationClass"), objectCastToClass);
		AssignmentExpression assignObjElse = new AssignmentExpression(new NamedTargetExpression("invocationClass"), new RegularMethodInvocation("getClass", new NamedTarget(objectParamName)));
		
		Block ifBody = new Block();
		ifBody.addStatement(new StatementExpression(assignObjIf));
		
		Block elseBody = new Block();
		elseBody.addStatement(new StatementExpression(assignObjElse));
		
		IfThenElseStatement invocationIte = new IfThenElseStatement(testObject, ifBody, elseBody);
		
		proceedMethodBody.addStatement(invocationIte);
		
		// Class[] types = new Class[_arguments.length];
		NamedTargetExpression argumentsDotLength = new NamedTargetExpression("length", new NamedTarget(argumentNameParamName));
						
		LocalVariableDeclarator typesArray = new LocalVariableDeclarator(new ArrayTypeReference(new BasicJavaTypeReference("Class")));
		ArrayCreationExpression typesArrayCreation = new ArrayCreationExpression(new BasicJavaTypeReference("Class"));
		typesArrayCreation.addDimensionInitializer(new FilledArrayIndex(argumentsDotLength.clone()));
		typesArray.add(new JavaVariableDeclaration("types", typesArrayCreation));

		/*
		 * 	For - loop
		 */
		
		// int i = 0;
		LocalVariableDeclarator loopVariable = new LocalVariableDeclarator(new BasicJavaTypeReference("int"));
		JavaVariableDeclaration loopVariableDecl = new JavaVariableDeclaration("i");
		loopVariableDecl.setInitialization(new RegularLiteral(new BasicTypeReference("int"), "0"));
		loopVariable.add(loopVariableDecl);
						
		// i < _arguments.length
		InfixOperatorInvocation condition = new InfixOperatorInvocation("<", new NamedTarget("i"));
		condition.addArgument(argumentsDotLength.clone());
		
		// i++
		StatementExpression incr = new StatementExpression(new PostfixOperatorInvocation("++", new NamedTarget("i")));
		StatementExprList update = new StatementExprList();
		update.addStatement(incr);
		
		// types[i] = _arguments[i].getClass()
		ArrayAccessExpression typesAccess = new ArrayAccessExpression(new NamedTargetExpression("types"));
		typesAccess.addIndex(new FilledArrayIndex(new NamedTargetExpression("i")));
	
		ArrayAccessExpression argumentsAccess = new ArrayAccessExpression(new NamedTargetExpression(argumentNameParamName));
		argumentsAccess.addIndex(new FilledArrayIndex(new NamedTargetExpression("i")));
		RegularMethodInvocation getClass = new RegularMethodInvocation("getClass", argumentsAccess);
		
		AssignmentExpression assignment = new AssignmentExpression(typesAccess, getClass);
		
		SimpleForControl forControl = new SimpleForControl(loopVariable, condition, update);
		
		Block forBody = new Block();
		forBody.addStatement(new StatementExpression(assignment));
		ForStatement forLoop = new ForStatement(forControl, forBody);
		
		/*
		 * 	End of For - loop
		 * 
		 * 	Try - catch with Method invocation
		 */
		
		// java.lang.reflect.Method m = _object.getClass().getMethod(_methodName, types);
		LocalVariableDeclarator method = new LocalVariableDeclarator(new BasicJavaTypeReference("java.lang.reflect.Method"));
		JavaVariableDeclaration methodDecl = new JavaVariableDeclaration("m");
		
		RegularMethodInvocation getMethod = new RegularMethodInvocation("getMethod", new NamedTargetExpression("invocationClass"));
		getMethod.addArgument(new NamedTargetExpression(methodNameParamName));
		getMethod.addArgument(new NamedTargetExpression("types"));
		
		methodDecl.setInitialization(getMethod);
		method.add(methodDecl);
		
		
		Block tryBody = new Block();
		tryBody.addStatement(method);
				
		
		// return (T) m.invoke(_object, _arguments);
		RegularMethodInvocation methodInvocation = new RegularMethodInvocation("invoke", new NamedTarget("m"));
		methodInvocation.addArgument(new NamedTargetExpression(objectParamName));
		methodInvocation.addArgument(new NamedTargetExpression(argumentNameParamName));
		ReturnStatement returnStatement = new ReturnStatement(new ClassCastExpression(new BasicTypeReference("T"), methodInvocation));
		
		tryBody.addStatement(returnStatement);
		
		TryStatement tryCatch = new TryStatement(tryBody);
		
		// catch (NoSuchMethodException nsm)
		Block nsmBody = new Block();
		
		// new try - catch block
		Block innerBody = new Block();
		LocalVariableDeclarator invocationClone = method.clone();
		((RegularMethodInvocation) invocationClone.variableDeclarations().get(0).initialization()).setName("getDeclaredMethod");
		innerBody.addStatement(invocationClone);
		
		// m.setAccessible(true);
		RegularMethodInvocation setAccessible = new RegularMethodInvocation("setAccessible", new NamedTarget("m"));
		setAccessible.addArgument(new RegularLiteral(new BasicTypeReference("boolean"), "true"));
		
		innerBody.addStatement(new StatementExpression(setAccessible));
		
		innerBody.addStatement(returnStatement.clone());

		TryStatement innerTryCatch = new TryStatement(innerBody);
		
		// All catch clauses required for reflection
		Block emptyCatchBody = new Block();
		emptyCatchBody.addStatement(new EmptyStatement());
		
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(new RegularMethodInvocation("getCause", new NamedTarget("invo")));
		rethrowBody.addStatement(rethrow);
		
		CatchClause catchIllegalArg = new CatchClause(new FormalParameter("iarg", new BasicTypeReference("IllegalArgumentException")), emptyCatchBody.clone());
		CatchClause catchSecurity = new CatchClause(new FormalParameter("se", new BasicTypeReference("SecurityException")), emptyCatchBody.clone());
		CatchClause catchIllegalAcc = new CatchClause(new FormalParameter("iac", new BasicTypeReference("IllegalAccessException")), emptyCatchBody.clone());
		CatchClause catchNoSuchMethod = new CatchClause(new FormalParameter("nsmInner", new BasicTypeReference("NoSuchMethodException")), emptyCatchBody.clone());
		CatchClause catchInvocationT = new CatchClause(new FormalParameter("invo", new BasicTypeReference("java.lang.reflect.InvocationTargetException")), rethrowBody.clone());
		
		innerTryCatch.addCatchClause(catchIllegalArg);
		innerTryCatch.addCatchClause(catchSecurity);
		innerTryCatch.addCatchClause(catchIllegalAcc);
		innerTryCatch.addCatchClause(catchInvocationT);
		innerTryCatch.addCatchClause(catchNoSuchMethod);
		
		
		nsmBody.addStatement(innerTryCatch);
		
		 // catch (NoSuchMethodException nsm) {
		tryCatch.addCatchClause(new CatchClause(new FormalParameter("nsm", new BasicTypeReference("NoSuchMethodException")), nsmBody));
		
		// All catch clauses required for reflection
		tryCatch.addCatchClause(catchIllegalArg.clone());
		tryCatch.addCatchClause(catchSecurity.clone());
		tryCatch.addCatchClause(catchIllegalAcc.clone());
		tryCatch.addCatchClause(catchInvocationT.clone());
		
		// Add a -  throw new Error(); - after the try {} catch, 
		// since the try-block should return anyway. If it doesn't, then an error occurred anyway
		ThrowStatement throwError = new ThrowStatement(new ConstructorInvocation(new BasicJavaTypeReference("Error"), null));
		
		/*
		 *	Add all the statements to the body
		 */
		proceedMethodBody.addStatement(typesArray);
		proceedMethodBody.addStatement(forLoop);
		proceedMethodBody.addStatement(tryCatch);
		proceedMethodBody.addStatement(throwError);
		
		proceedMethod.setImplementation(new RegularImplementation(proceedMethodBody));
		
		// Add the proceed method to the aspect
		aspectClass.add(proceedMethod);
	}
}
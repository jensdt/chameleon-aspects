package chameleon.aspects.advice.types.translation.methodInvocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jnome.core.expression.ArrayAccessExpression;
import jnome.core.expression.ArrayCreationExpression;
import jnome.core.expression.ArrayInitializer;
import jnome.core.expression.ClassLiteral;
import jnome.core.expression.invocation.ConstructorInvocation;
import jnome.core.language.Java;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.variable.JavaVariableDeclaration;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.Aspect;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.reflectiveinvocation.MethodCoordinator;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeArgumentsTypeCheck;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeIfCheck;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeTypeCheck;
import chameleon.aspects.advice.types.translation.ReflectiveAdviceTransformationProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.ArgsPointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.IfPointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.TargetTypePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.ThisTypePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.TypePointcutExpression;
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
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.FilledArrayIndex;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.EmptyStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public abstract class ReflectiveMethodInvocation extends ReflectiveAdviceTransformationProvider {

	public final String methodNameParamName = "_$methodName";
	public final String argumentNameParamName = "_$arguments";
	public final String argumentIndexParamName = "_$argumentIndex";
	public final String typesParamName = "_$types";
	public final String retvalName = "_$retval";
	
	private Advice advice; 
	
	public ReflectiveMethodInvocation(MatchResult<? extends PointcutExpression, ? extends MethodInvocation> joinpoint) {
		super(joinpoint);
	}
	
	@Override
	public MatchResult<? extends PointcutExpression, ? extends MethodInvocation> getJoinpoint() {
		return (MatchResult<? extends PointcutExpression, ? extends MethodInvocation>) super.getJoinpoint();
	}
	
	public Advice advice() {
		return advice;
	}
	
	protected abstract Block getInnerBody() throws LookupException;
	
	public List<TypeExceptionDeclaration> getCheckedExceptionsWithoutSubtypes(Method method) throws LookupException {
		List<TypeExceptionDeclaration> checkedTypeExceptions = new ArrayList<TypeExceptionDeclaration>();
		
		// Copy all checked exceptions
		for (ExceptionDeclaration exception : method.getExceptionClause().exceptionDeclarations()) {
			if (exception instanceof TypeExceptionDeclaration && ((ObjectOrientedLanguage) method.language(ObjectOrientedLanguage.class)).isCheckedException(((TypeExceptionDeclaration) exception).getType()))
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
	
	public Block getRethrowBody(NamedTargetExpression target) {
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(target);

		rethrowBody.addStatement(rethrow);
		
		return rethrowBody;
	}
	
	public TryStatement getEnclosingTry(Block tryBody) throws LookupException {
		// Re-throw unchecked exceptions (subclasses of RuntimeException )	
		TryStatement exceptionHandler = new TryStatement(tryBody);
		exceptionHandler.addCatchClause(new CatchClause(new FormalParameter("unchecked", new BasicTypeReference("RuntimeException")), getRethrowBody(new NamedTargetExpression("unchecked"))));

		// Add a re-throw for each checked exception
		int exceptionIndex = 0;
		List<TypeExceptionDeclaration> checkedTypeExceptions = getCheckedExceptionsWithoutSubtypes(getJoinpoint().getJoinpoint().getElement());
		
		for (TypeExceptionDeclaration exception : checkedTypeExceptions) {
			exceptionHandler.addCatchClause(getRethrow("ex" + exceptionIndex, exception.getTypeReference().clone()));
			
			exceptionIndex++;
		}
		
		// Add a catch all. This isn't actually necessary since we already handled all cases, but since the generic proceed method throws a throwable we need it to prevent compile errors
		exceptionHandler.addCatchClause(getCatchAll());
		
		return exceptionHandler;
	}
	
	public CatchClause getRethrow(String name, TypeReference type) {
		return new CatchClause(new FormalParameter(name, type), getRethrowBody(new NamedTargetExpression(name)));
	}
	
	public CatchClause getCatchAll() {
		Block emptyCatchBody = new Block();
		emptyCatchBody.addStatement(new EmptyStatement());
		
		return new CatchClause(new FormalParameter("thrwbl", new BasicTypeReference("Throwable")), emptyCatchBody);
	}

	protected Statement getDieStatement() {
		ThrowStatement throwError = new ThrowStatement(new ConstructorInvocation(new BasicJavaTypeReference("Error"), null));
		return throwError;
	}

	@Override
	public NormalMethod transform(Advice advice) throws LookupException {
		this.advice = advice;
		
		Aspect<?> aspect = advice.aspect();
		CompilationUnit compilationUnit = aspect.nearestAncestor(CompilationUnit.class);
		
		// Get the class we are going to create this method in
		RegularType aspectClass = getOrCreateAspectClass(compilationUnit, aspect.name());
			
		// Get the naming registries
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		NamingRegistry<Method> methodNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("javamethod");			
		
		Method m = getJoinpoint().getJoinpoint().getElement();
		final String adviceMethodName = "advice_" + adviceNamingRegistry.getName(advice) + "_" + methodNamingRegistry.getName(m);
		
		// Check if this method has already been defined (which is possible, if a joinpoint to the same method is found multiple times)
		List<NormalMethod> definedMethods = compilationUnit.descendants(NormalMethod.class, new SafePredicate<NormalMethod>() {
			@Override
			public boolean eval(NormalMethod object) {
				return object.name().equals(adviceMethodName);
			}
		});
		
		if (!definedMethods.isEmpty())
			return null;
		
		
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
		
		FormalParameter callee = new FormalParameter(calleeName, new BasicTypeReference("Object"));
		header.addFormalParameter(callee);
		
		// Get the body
		Block body = getBody();
		
		// Set the method body
		adviceMethod.setImplementation(new RegularImplementation(body));
		
		// Add the method
		aspectClass.add(adviceMethod);
		
		return adviceMethod;
	}
	
	protected Block getBody() throws LookupException {
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
			TryStatement exceptionHandler = getEnclosingTry(adviceBody);
		
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
			ArrayAccessExpression argumentsIndexAccess = new ArrayAccessExpression(argumentsIndexTarget);
			argumentsIndexAccess.addIndex(new FilledArrayIndex(new RegularLiteral(new BasicJavaTypeReference("int"), Integer.toString(index))));

			ArrayAccessExpression argumentsAccess = new ArrayAccessExpression(argumentsTarget);
			argumentsAccess.addIndex(new FilledArrayIndex(argumentsIndexAccess));

			// Add the cast, since the arguments is just an Object array
			// Mind boxable-unboxable types
			Java java = fp.language(Java.class);
			
			TypeReference typeToCastTo = null;
			try {
				if (fp.getTypeReference().getType().isTrue(fp.language().property("primitive")))
					typeToCastTo = new BasicTypeReference (java.box(fp.getTypeReference().getType()).getFullyQualifiedName());
				else
					typeToCastTo = fp.getTypeReference().clone();
			} catch (LookupException e) {
				System.out.println("Lookupexception while boxing");
			}
				
			
			ClassCastExpression cast = new ClassCastExpression(typeToCastTo, argumentsAccess);
			
			parameterInjectorDecl.setInitialization(cast);
			parameterInjector.add(parameterInjectorDecl);
			
			declarations.add(parameterInjector);
		}
		
		return declarations;
	}
	
	public RegularMethodInvocation createProceedInvocation(InvocationTarget aspectClassTarget, Expression objectTarget, Expression methodNameTarget, Expression argumentsTarget) throws LookupException {
		RegularMethodInvocation proceedInvocation = new RegularMethodInvocation("proceed", aspectClassTarget);
		proceedInvocation.addArgument((new BasicTypeArgument(new BasicTypeReference("T"))));
		
		proceedInvocation.addArgument(objectTarget);
		proceedInvocation.addArgument(methodNameTarget);
		proceedInvocation.addArgument(argumentsTarget);
		
		ArrayCreationExpression typesArray = new ArrayCreationExpression(new ArrayTypeReference(new BasicJavaTypeReference("Class")));
		ArrayInitializer typesInitializer = new ArrayInitializer();					
	
		for (FormalParameter fp : (List<FormalParameter>) getJoinpoint().getJoinpoint().getElement().formalParameters())
			typesInitializer.addInitializer(new ClassLiteral(fp.getTypeReference().clone()));
		
		typesArray.setInitializer(typesInitializer);
		
		proceedInvocation.addArgument(typesArray);
		
		
		return proceedInvocation;
	}

	@Override
	protected NormalMethod getReflectiveMethodDefinition() {
		NormalMethod method = super.getReflectiveMethodDefinition();
		
		method.getExceptionClause().add(new TypeExceptionDeclaration(new BasicJavaTypeReference("Throwable")));
		
		return method;
	}
	
	@Override
	protected Block getReflectivePublicCall() {
		Block publicCall = new Block();
		
		LocalVariableDeclarator method = new LocalVariableDeclarator(new BasicJavaTypeReference("java.lang.reflect.Method"));
		JavaVariableDeclaration methodDecl = new JavaVariableDeclaration("m");
		
		RegularMethodInvocation getMethod = new RegularMethodInvocation("getMethod", new NamedTargetExpression("invocationClass"));
		getMethod.addArgument(new NamedTargetExpression(methodNameParamName));
		getMethod.addArgument(new NamedTargetExpression(typesParamName));
		
		methodDecl.setInitialization(getMethod);
		method.add(methodDecl);
		
		publicCall.addStatement(method);
				
		
		// return (T) m.invoke(_object, _arguments);
		RegularMethodInvocation methodInvocation = new RegularMethodInvocation("invoke", new NamedTarget("m"));
		methodInvocation.addArgument(new NamedTargetExpression(objectParamName));
		methodInvocation.addArgument(new NamedTargetExpression(argumentNameParamName));
		ReturnStatement returnStatement = new ReturnStatement(new ClassCastExpression(new BasicTypeReference("T"), methodInvocation));
		
		publicCall.addStatement(returnStatement);
		
		return publicCall;
	}
	
	protected List<CatchClause> getIgnoredPrivateCatchClauses() {
		List<CatchClause> ignoredClauses = super.getIgnoredPrivateCatchClauses();
		
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(new RegularMethodInvocation("getCause", new NamedTarget("invo")));
		rethrowBody.addStatement(rethrow);
		
		ignoredClauses.add(new CatchClause(new FormalParameter("invo", new BasicTypeReference("java.lang.reflect.InvocationTargetException")), rethrowBody));
		
		return ignoredClauses;
	}
	
	@Override
	protected Block getReflectivePrivateCall() {
		Block privateCall = new Block();
		
		LocalVariableDeclarator method = new LocalVariableDeclarator(new BasicJavaTypeReference("java.lang.reflect.Method"));
		JavaVariableDeclaration methodDecl = new JavaVariableDeclaration("m");
		
		RegularMethodInvocation getMethod = new RegularMethodInvocation("getMethod", new NamedTargetExpression("invocationClass"));
		getMethod.addArgument(new NamedTargetExpression(methodNameParamName));
		getMethod.addArgument(new NamedTargetExpression(typesParamName));
		
		methodDecl.setInitialization(getMethod);
		method.add(methodDecl);
		
		privateCall.addStatement(method);
				
		
		// return (T) m.invoke(_object, _arguments);
		RegularMethodInvocation methodInvocation = new RegularMethodInvocation("invoke", new NamedTarget("m"));
		methodInvocation.addArgument(new NamedTargetExpression(objectParamName));
		methodInvocation.addArgument(new NamedTargetExpression(argumentNameParamName));
		ReturnStatement returnStatement = new ReturnStatement(new ClassCastExpression(new BasicTypeReference("T"), methodInvocation));
		
		privateCall.addStatement(returnStatement);
		
		return privateCall;
	}
	
	@Override
	protected CatchClause getNotFoundCatchClause() {
		return new CatchClause(new FormalParameter("nsm", new BasicTypeReference("NoSuchMethodException")), new Block());
	}
	
	@Override
	protected List<FormalParameter> getReflectiveMethodParameters() {
		List<FormalParameter> resultList = new ArrayList<FormalParameter>();
		
		resultList.add(new FormalParameter(objectParamName, new BasicTypeReference("Object")));
		resultList.add(new FormalParameter(methodNameParamName, new BasicTypeReference("String")));
		resultList.add(new FormalParameter(argumentNameParamName, new ArrayTypeReference(new BasicJavaTypeReference("Object"))));
		resultList.add(new FormalParameter(typesParamName, new ArrayTypeReference(new BasicJavaTypeReference("Class"))));
		
		return resultList;
	}

	@Override
	protected String getReflectiveMethodName() {
		return "proceed";
	}
	


	@Override
	public boolean canTransform(RuntimePointcutExpression pointcutExpression) {
		if (super.canTransform(pointcutExpression))
			return true;
		
		if (pointcutExpression instanceof ArgsPointcutExpression)
			return true;
		
		return false;
	}

	@Override
	public RuntimeExpressionProvider getRuntimeTransformer(RuntimePointcutExpression pointcutExpression) {
		if (pointcutExpression instanceof ArgsPointcutExpression)
			return new RuntimeArgumentsTypeCheck(new NamedTargetExpression(argumentNameParamName));
		
		if (pointcutExpression instanceof ThisTypePointcutExpression)
			return new RuntimeTypeCheck(new NamedTargetExpression(calleeName));

		if (pointcutExpression instanceof TargetTypePointcutExpression)
			return new RuntimeTypeCheck(new NamedTargetExpression(
					objectParamName));

		if (pointcutExpression instanceof IfPointcutExpression)
			return new RuntimeIfCheck();

		return null;
	}
	
	@Override
	public Coordinator<NormalMethod> getCoordinator() {
		return new MethodCoordinator(this, getJoinpoint());
	}
	
}
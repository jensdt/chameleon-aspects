package chameleon.aspects.advice.types.translation;

import java.util.ArrayList;
import java.util.List;

import jnome.core.expression.invocation.ConstructorInvocation;
import jnome.core.type.BasicJavaTypeReference;
import jnome.core.variable.JavaVariableDeclaration;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.reflectiveinvocation.MethodCoordinator;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeArgumentsTypeCheck;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeIfCheck;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeTypeCheck;
import chameleon.aspects.pointcut.expression.MatchResult;
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
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.method.RegularImplementation;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.RegularMemberVariable;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.support.expression.AssignmentExpression;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.variable.MemberVariableDeclarator;
import chameleon.support.modifier.Constructor;
import chameleon.support.modifier.Private;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.StatementExpression;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public abstract class ReflectiveAdviceTransformationProvider extends AbstractAdviceTransformationProvider<NormalMethod> {
	
	public ReflectiveAdviceTransformationProvider(MatchResult joinpoint) {
		super(joinpoint);
	}

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
			aspectClass.addModifier(new Public());
			
			// Create new empty constructor
			NormalMethod m = new NormalMethod(new SimpleNameDeclarationWithParametersHeader(name), null);
			m.addModifier(new Constructor());
			m.addModifier(new Private());
			m.setImplementation(new RegularImplementation(new Block()));
			
			aspectClass.add(m);
			
			// Add instance variable
			MemberVariableDeclarator decl = new MemberVariableDeclarator(new BasicTypeReference(name));
			VariableDeclaration varDecl = new VariableDeclaration("instance");
			
			decl.add(varDecl);
			decl.addModifier(new Static());
			decl.addModifier(new Private());
			varDecl.setInitialization(new ConstructorInvocation(new BasicJavaTypeReference(name), null));
			aspectClass.add(decl);
			
			// Getter for the instance
			NormalMethod getter = new NormalMethod(new SimpleNameDeclarationWithParametersHeader("instance"), new BasicTypeReference(name));
			getter.addModifier(new Static());
			getter.addModifier(new Public());
			
			Block getterBody = new Block();
			getterBody.addStatement(new ReturnStatement(new NamedTargetExpression("instance")));
			getter.setImplementation(new RegularImplementation(getterBody));
			aspectClass.add(getter);
			
			compilationUnit.namespacePart(1).add(aspectClass);
		} else {
			// Aspect class already exist!
			aspectClass = aspectClasses.get(0);
		}
		
		// Add the method used to make reflective calls
		createReflectiveMethod(aspectClass); 
		
		return aspectClass;
	}
	
	protected boolean hasMethodWithName(RegularType type, final String methodName) {
		return type.hasDescendant(NormalMethod.class, new SafePredicate<NormalMethod>() {

			@Override
			public boolean eval(NormalMethod object) {
				return object.signature().name().equals(methodName);
			}
		});
	}
	
	protected abstract String getReflectiveMethodName();
	
	protected DeclarationWithParametersHeader getReflectiveMethodHeader() {
		DeclarationWithParametersHeader header = new SimpleNameDeclarationWithParametersHeader(getReflectiveMethodName());
		
		for (FormalParameter fp : getReflectiveMethodParameters())
			header.addFormalParameter(fp);
		
		header.addTypeParameter(new FormalTypeParameter(new SimpleNameSignature("T")));

		return header;
	}
	
	protected abstract List<FormalParameter> getReflectiveMethodParameters();
	
	protected List<CatchClause> getIgnoredPublicCatchClauses() {
		List<CatchClause> ignoredClauses = getIgnoredPrivateCatchClauses();
		
		CatchClause notFoundCatchClause = getNotFoundCatchClause();
		notFoundCatchClause.getExceptionParameter().setName("nsm_inner");
		
		ignoredClauses.add(notFoundCatchClause);
		
		return ignoredClauses;
	}
	
	protected List<CatchClause> getIgnoredPrivateCatchClauses() {
		List<CatchClause> ignoredClauses = new ArrayList<CatchClause>();
		
		ignoredClauses.add(new CatchClause(new FormalParameter("iarg", new BasicTypeReference("IllegalArgumentException")), new Block()));
		ignoredClauses.add(new CatchClause(new FormalParameter("se", new BasicTypeReference("SecurityException")), new Block()));
		ignoredClauses.add(new CatchClause(new FormalParameter("iac", new BasicTypeReference("IllegalAccessException")), new Block()));
			
		return ignoredClauses;
	}
	
	protected abstract CatchClause getNotFoundCatchClause();
	
	protected NormalMethod getReflectiveMethodDefinition() {
		NormalMethod method = new NormalMethod(getReflectiveMethodHeader(), new BasicTypeReference("T"));
		
		method.addModifier(new Private());
		method.addModifier(new Static());
		
		return method;
	}
	
	protected Block getReflectiveMethodBody() {
		Block body = new Block();
		
		// Get the initialization
		body.addBlock(getReflectiveMethodBodyInitialisation());
		
		// Create the try-catch with the reflective call for public members
		TryStatement publicTry = new TryStatement(getReflectivePublicCall());
		
		// Add each ignored-catch-clause
		publicTry.addAllCatchClauses(getIgnoredPrivateCatchClauses());
		
		// Get the catch clause that indicates the member wasn't found
		CatchClause notFound = getNotFoundCatchClause();
		
		// Set the not-found body the a new try-catch with the private call
		TryStatement privateTry = new TryStatement(getReflectivePrivateCall());
		privateTry.addAllCatchClauses(getIgnoredPublicCatchClauses());
		
		Block privateTryStatement = new Block();
		privateTryStatement.addStatement(privateTry);
		notFound.setStatement(privateTryStatement);
		
		publicTry.addCatchClause(notFound);
		
		body.addStatement(publicTry);
		
		// Add a die statement
		body.addStatement(getDieStatement());
		
		return body;
	}
	
	protected Statement getDieStatement() {
		ThrowStatement throwError = new ThrowStatement(new ConstructorInvocation(new BasicJavaTypeReference("Error"), null));
		return throwError;
	}
	
	protected abstract Block getReflectivePublicCall();
	protected abstract Block getReflectivePrivateCall();
	
	public final String objectParamName = "_$object";
	public final String calleeName = "_$callee";
	
	protected Block getReflectiveMethodBodyInitialisation() {
		Block initialisation = new Block();
		
		LocalVariableDeclarator invocationClass = new LocalVariableDeclarator(new BasicJavaTypeReference("Class"));
		JavaVariableDeclaration invocationClassDecl = new JavaVariableDeclaration("invocationClass");
		
		invocationClass.add(invocationClassDecl);
		initialisation.addStatement(invocationClass);
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
		
		initialisation.addStatement(invocationIte);
		
		return initialisation;
	}

	protected void createReflectiveMethod(RegularType aspectClass) {
		// Terminate if the method already exists
		if (hasMethodWithName(aspectClass, getReflectiveMethodName()))
			return;
		
		// Get the method definition
		NormalMethod method = getReflectiveMethodDefinition();
		
		// Get the method body
		Block methodBody = getReflectiveMethodBody();
		
		// Attach the body
		method.setImplementation(new RegularImplementation(methodBody));
		
		// Add the method
		aspectClass.add(method);
	}
	@Override
	public boolean canTransform(RuntimePointcutExpression pointcutExpression) {
		if (pointcutExpression instanceof TypePointcutExpression)
			return true;
		
		if (pointcutExpression instanceof IfPointcutExpression)
			return true;
		
		return false;
	}

	@Override
	public RuntimeExpressionProvider getRuntimeTransformer(RuntimePointcutExpression pointcutExpression) {		
		if (pointcutExpression instanceof ThisTypePointcutExpression)
			return new RuntimeTypeCheck(new NamedTargetExpression(calleeName));
		
		if (pointcutExpression instanceof TargetTypePointcutExpression)
			return new RuntimeTypeCheck(new NamedTargetExpression(objectParamName));
		
		if (pointcutExpression instanceof IfPointcutExpression)
			return new RuntimeIfCheck();
		
		return null;
	}
	
	@Override
	protected abstract Coordinator<NormalMethod> getCoordinator();
}

package chameleon.aspects.advice.types.translation.reflection.fieldaccess;

import java.util.ArrayList;
import java.util.List;

import jnome.core.type.BasicJavaTypeReference;
import jnome.core.variable.JavaVariableDeclaration;
import chameleon.aspects.Aspect;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.runtimetransformation.Coordinator;
import chameleon.aspects.advice.runtimetransformation.reflectiveinvocation.FieldCoordinator;
import chameleon.aspects.advice.types.translation.reflection.ReflectiveAdviceTransformationProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.namingRegistry.NamingRegistryFactory;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.RegularImplementation;
import chameleon.core.statement.Block;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.RegularType;
import chameleon.oo.type.TypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.oo.type.generics.FormalTypeParameter;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.modifier.Public;
import chameleon.support.modifier.Static;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.StatementExpression;
import chameleon.support.variable.LocalVariable;
import chameleon.support.variable.LocalVariableDeclarator;

public abstract class ReflectiveFieldRead extends ReflectiveAdviceTransformationProvider {

	public ReflectiveFieldRead(MatchResult<?, ?> joinpoint) {
		super(joinpoint);
	}
	
	public final String fieldName = "_$field";
	public final String retvalName = "_$retval";
	
	@Override
	protected List<FormalParameter> getReflectiveMethodParameters() {
		List<FormalParameter> resultList = new ArrayList<FormalParameter>();
		
		resultList.add(new FormalParameter(objectParamName, new BasicTypeReference<BasicTypeReference<?>>("Object")));
		resultList.add(new FormalParameter(fieldName, new BasicTypeReference<BasicTypeReference<?>>("String")));
		
		return resultList;
	}
	
	@Override
	protected Block getReflectivePublicCall() {
		Block publicCall = new Block();
		
		// java.lang.reflect.Field f = invocationClass.getField(fieldName);
		LocalVariableDeclarator field = new LocalVariableDeclarator(new BasicJavaTypeReference("java.lang.reflect.Field"));
		JavaVariableDeclaration<LocalVariable> fieldDecl = new JavaVariableDeclaration<LocalVariable>("f");
		
		RegularMethodInvocation<?> getMethod = new RegularMethodInvocation("getField", new NamedTargetExpression("invocationClass"));
		getMethod.addArgument(new NamedTargetExpression(fieldName));
		
		fieldDecl.setInitialization(getMethod);
		field.add(fieldDecl);
		
		publicCall.addStatement(field);
		
		// return (T) f.get(_object);
		RegularMethodInvocation<?> methodInvocation = new RegularMethodInvocation("get", new NamedTarget("f"));
		methodInvocation.addArgument(new NamedTargetExpression(objectParamName));
		ReturnStatement returnStatement = new ReturnStatement(new ClassCastExpression(new BasicTypeReference("T"), methodInvocation));
		
		publicCall.addStatement(returnStatement);
		
		return publicCall;
	}
	
	@Override
	protected Block getReflectivePrivateCall() {
		Block privateCall = new Block();
		
		// java.lang.reflect.Field f = invocationClass.getField(fieldName);
		LocalVariableDeclarator field = new LocalVariableDeclarator(new BasicJavaTypeReference("java.lang.reflect.Field"));
		JavaVariableDeclaration fieldDecl = new JavaVariableDeclaration("f");
		
		RegularMethodInvocation getMethod = new RegularMethodInvocation("getDeclaredField", new NamedTargetExpression("invocationClass"));
		getMethod.addArgument(new NamedTargetExpression(fieldName));
		
		fieldDecl.setInitialization(getMethod);
		field.add(fieldDecl);
		
		privateCall.addStatement(field);
		
		// f.setAccessible(true);
		RegularMethodInvocation setAccessible = new RegularMethodInvocation("setAccessible", new NamedTarget("f"));
		setAccessible.addArgument(new RegularLiteral(new BasicTypeReference("boolean"), "true"));
		
		privateCall.addStatement(new StatementExpression(setAccessible));
		
		// return (T) f.get(_object);
		RegularMethodInvocation methodInvocation = new RegularMethodInvocation("get", new NamedTarget("f"));
		methodInvocation.addArgument(new NamedTargetExpression(objectParamName));
		ReturnStatement returnStatement = new ReturnStatement(new ClassCastExpression(new BasicTypeReference("T"), methodInvocation));
		
		privateCall.addStatement(returnStatement);
		
		return privateCall;
	}
	
	@Override
	protected CatchClause getNotFoundCatchClause() {
		return new CatchClause(new FormalParameter("nsm", new BasicTypeReference("NoSuchFieldException")), new Block());
	}
	
	public RegularMethodInvocation createGetFieldValueInvocation(NamedTarget aspectClassTarget, NamedTargetExpression objectTarget, NamedTargetExpression fieldNameTarget) {
		RegularMethodInvocation getInstance = new RegularMethodInvocation("instance", aspectClassTarget);
		RegularMethodInvocation getFieldValueInvocation = new RegularMethodInvocation("getFieldValue", getInstance);
		getFieldValueInvocation.addArgument((new BasicTypeArgument(new BasicTypeReference("T"))));
		
		getFieldValueInvocation.addArgument(objectTarget);
		getFieldValueInvocation.addArgument(fieldNameTarget);
		
		return getFieldValueInvocation;
	}
	
	@Override
	public String getAdviceMethodName(Advice advice) {
		NamingRegistry<Advice> adviceNamingRegistry = NamingRegistryFactory.instance().getNamingRegistryFor("advice");
		
		return "advice_" + adviceNamingRegistry.getName(advice);
	}
	
	@Override
	public NormalMethod transform(Advice<?> advice) throws LookupException {
		this.advice = advice;
		Aspect<?> aspect = advice.aspect();
		CompilationUnit compilationUnit = aspect.nearestAncestor(CompilationUnit.class);
		
		// Get the class we are going to create this method in
		RegularType aspectClass = getOrCreateAspectClass(compilationUnit, aspect.name());
		
		// Check if the method has already been created
		if (isAlreadyDefined(advice, compilationUnit))
			return null;
		
		String adviceMethodName = getAdviceMethodName(advice);
		
		DeclarationWithParametersHeader header = new SimpleNameDeclarationWithParametersHeader(adviceMethodName);
		
		TypeReference returnType = new BasicTypeReference("T");			
		NormalMethod adviceMethod = new NormalMethod(header, returnType);
		
		adviceMethod.addModifier(new Public());
		adviceMethod.addModifier(new Static());
		
		header.addTypeParameter(new FormalTypeParameter(new SimpleNameSignature("T")));

		// Add all the parameters to allow the reflective invocation 
		FormalParameter object = new FormalParameter(objectParamName, new BasicTypeReference("Object"));
		header.addFormalParameter(object);
		
		FormalParameter methodName = new FormalParameter(fieldName, new BasicTypeReference("String"));
		header.addFormalParameter(methodName);
		
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
	
	protected abstract Block getBody();

	protected Advice<?> advice;
	
	public Advice<?> advice() {
		return this.advice;
	}
	
	@Override
	protected String getReflectiveMethodName() {
		return "getFieldValue";
	}
	
	@Override
	public Coordinator<NormalMethod> getCoordinator() {
		return new FieldCoordinator(this, getJoinpoint());
	}
}

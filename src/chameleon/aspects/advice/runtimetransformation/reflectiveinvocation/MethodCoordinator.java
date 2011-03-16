package chameleon.aspects.advice.runtimetransformation.reflectiveinvocation;

import java.util.List;

import chameleon.aspects.advice.runtimetransformation.AbstractCoordinator;
import chameleon.aspects.advice.types.translation.methodInvocation.ReflectiveMethodInvocation;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.IfPointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.RegularImplementation;
import chameleon.core.method.exception.TypeExceptionDeclaration;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;
import chameleon.support.statement.ThrowStatement;
import chameleon.support.statement.TryStatement;

/**
 * 	The coordinator for method invocations. Implements a two-phased transformation phase:
 * 
 * 		* First phase: arguments and type checks
 * 		* Second phase, after parameter injection: if-checks (since these can access the parameters!)
 * 
 * 	@author Jens
 */
public class MethodCoordinator extends AbstractCoordinator<NormalMethod> {

	/**
	 * 	Constructor
	 * 
	 * 	@param 	adviceTransformationProvider
	 * 			The given transformation provider
	 * 	@param 	matchResult
	 * 			The joinpoint
	 */
	public MethodCoordinator(ReflectiveMethodInvocation adviceTransformationProvider, MatchResult matchResult) {
		super(adviceTransformationProvider, matchResult);
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void transform(NormalMethod element) {
		if (element == null)
			return;
		
		// First, get all the runtime pointcut expressions but maintain the structure (and/or/...)
		PointcutExpression prunedTree = getMatchResult().getExpression().getPrunedTree(RuntimePointcutExpression.class);
		
		if (prunedTree == null)
			return;
		
		// Now, select all pointcut expressions we can weave at the start of the method.
		// Currently, this is all except the 'if'-expression, since it can refer to the value of parameters
		PointcutExpression initialCheckTree = prunedTree.removeFromTree(IfPointcutExpression.class);
		
		NamingRegistry<RuntimePointcutExpression> expressionNames = new NamingRegistry<RuntimePointcutExpression>();
		Block finalBody = new Block();
		
		if (initialCheckTree != null) {
			finalBody.addBlock(addTest(initialCheckTree, expressionNames));
		}
		
		// Part two: add the check for the 'if' expressions
		Statement lastTry = null;
		
		for (Statement st : element.body().statements())
			if (st instanceof TryStatement)
				lastTry = st;
		
		finalBody.addStatements(element.body().statementsBefore(lastTry));
		
		// Get the if-expressions
		PointcutExpression ifExprTree = prunedTree.getPrunedTree(IfPointcutExpression.class);
		
		if (ifExprTree != null) {
			finalBody.addBlock(addTest(ifExprTree, expressionNames));
		}
		// Add the rest of the method
		finalBody.addStatements(element.body().statementsAfter(lastTry));
		
		element.setImplementation(new RegularImplementation(finalBody));
		
	}
	
	/**
	 * 	Add the test corresponding to the items in the tree
	 * 
	 * 	@param 	tree
	 * 			The expression tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 */
	@Override
	protected Block addTest(PointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		Block body = new Block();
		
		// Re-throw unchecked exceptions (subclasses of RuntimeException )	
		TryStatement exceptionHandler = null;
		try {
			Block tryBody = new Block();
			tryBody.addStatement(super.addTest(tree, expressionNames));
			exceptionHandler = new TryStatement(tryBody);
			exceptionHandler.addCatchClause(getRethrow("unchecked", new BasicTypeReference("RuntimeException")));
	
			// Add a re-throw for each checked exception
			int exceptionIndex = 0;
			List<TypeExceptionDeclaration> checkedTypeExceptions = getAdviceTransformationProvider().getCheckedExceptionsWithoutSubtypes(getMatchResult().getJoinpoint().getElement());
			
			for (TypeExceptionDeclaration exception : checkedTypeExceptions) {
				exceptionHandler.addCatchClause(getRethrow("ex" + exceptionIndex, exception.getTypeReference().clone()));
				
				exceptionIndex++;
			}
			
			// Add a catch all. This isn't actually necessary since we already handled all cases, but since the generic proceed method throws a throwable we need it to prevent compile errors
			exceptionHandler.addCatchClause(getAdviceTransformationProvider().getCatchAll());
		} catch (LookupException e){
			// Will only occur with a bug, not in normal usage
			System.out.println("Creating surrounding try in runtime check threw LookupEx");
			e.printStackTrace();
			
		}
		
		body.addStatement(exceptionHandler);
		
		return body;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	protected Block getTerminateBody() {
		Block terminateBody = new Block();
		terminateBody.addStatement(new ReturnStatement(getProceedInvocation()));
		
		return terminateBody;
	}
	
	/**
	 * 	Get a re-throw clause
	 * 
	 * 	@param 	name
	 * 			The name of the exception parameter
	 * 	@param 	type
	 * 			The type of the exception parameter
	 * 	@return
	 */
	private CatchClause getRethrow(String name, TypeReference type) {
		Block rethrowBody = new Block();
		ThrowStatement rethrow = new ThrowStatement(new NamedTargetExpression(name));

		rethrowBody.addStatement(rethrow);
		
		return new CatchClause(new FormalParameter(name, type), rethrowBody);
	}
	
	/**
	 * 	Get the proceed invocation
	 * 
	 * 	@return	The proceed invocation
	 */
	private RegularMethodInvocation getProceedInvocation() {
		try {
			return getAdviceTransformationProvider().createProceedInvocation(new NamedTarget(getAdviceTransformationProvider().advice().aspect().name()), new NamedTargetExpression(getAdviceTransformationProvider().objectParamName), new NamedTargetExpression(getAdviceTransformationProvider().methodNameParamName), new NamedTargetExpression(getAdviceTransformationProvider().argumentNameParamName));
		} catch (LookupException e) {
			System.out.println("creating proceed invocation threw lookupEx");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public ReflectiveMethodInvocation getAdviceTransformationProvider() {
		return (ReflectiveMethodInvocation) super.getAdviceTransformationProvider();
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public MatchResult<? extends PointcutExpression, ? extends MethodInvocation> getMatchResult() {
		return (MatchResult<? extends PointcutExpression, ? extends MethodInvocation>) super.getMatchResult();
	}
}
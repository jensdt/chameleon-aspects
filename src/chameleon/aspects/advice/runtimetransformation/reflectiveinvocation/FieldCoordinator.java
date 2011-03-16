package chameleon.aspects.advice.runtimetransformation.reflectiveinvocation;

import java.util.List;

import chameleon.aspects.advice.runtimetransformation.AbstractCoordinator;
import chameleon.aspects.advice.types.translation.fieldaccess.ReflectiveFieldRead;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.method.RegularImplementation;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.support.member.simplename.method.NormalMethod;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.statement.ReturnStatement;

/**
 * 	The coordinator for method invocations. Implements a single-phased transformation phase.
 * 
 * 	@author Jens
 */
public class FieldCoordinator extends AbstractCoordinator<NormalMethod> {

	/**
	 * 	{@inheritDoc}
	 */
	public FieldCoordinator(ReflectiveFieldRead adviceTransformationProvider, MatchResult matchResult) {
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
		
		NamingRegistry<RuntimePointcutExpression> expressionNames = new NamingRegistry<RuntimePointcutExpression>();
		Block finalBody = addTest(prunedTree, expressionNames);
		
		finalBody.addBlock(element.body());
		element.setImplementation(new RegularImplementation(finalBody));
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
	 * 	Get the proceed invocation
	 * 
	 * 	@return	The proceed invocation
	 */
	private RegularMethodInvocation getProceedInvocation() {
		return getAdviceTransformationProvider().createGetFieldValueInvocation(new NamedTarget(getAdviceTransformationProvider().advice().aspect().name()), new NamedTargetExpression(getAdviceTransformationProvider().objectParamName), new NamedTargetExpression(getAdviceTransformationProvider().fieldName));
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public ReflectiveFieldRead getAdviceTransformationProvider() {
		return (ReflectiveFieldRead) super.getAdviceTransformationProvider();
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public MatchResult<? extends PointcutExpression, ? extends MethodInvocation> getMatchResult() {
		return (MatchResult<? extends PointcutExpression, ? extends MethodInvocation>) super.getMatchResult();
	}
}
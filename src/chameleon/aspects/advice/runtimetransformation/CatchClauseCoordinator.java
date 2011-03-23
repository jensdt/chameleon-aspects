package chameleon.aspects.advice.runtimetransformation;

import java.util.List;

import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.IfPointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableDeclaration;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import chameleon.support.statement.CatchClause;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.variable.LocalVariableDeclarator;

public class CatchClauseCoordinator extends AbstractCoordinator<Block> {

	private final Block originalBody;
	
	public CatchClauseCoordinator(RuntimeTransformationProvider adviceTransformationProvider, MatchResult<?, ?> matchResult, Block originalBody) {
		super(adviceTransformationProvider, matchResult);
		this.originalBody = originalBody;
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public void transform(Block element, List<FormalParameter> parameters) {
		if (element == null)
			return;
		
		// Part one: get all the runtime pointcut expressions but maintain the structure (and/or/...)
		PointcutExpression prunedTree = getMatchResult().getExpression().getPrunedTree(RuntimePointcutExpression.class);
		
		if (prunedTree == null)
			return;
		
		// Now, select all pointcut expressions we can weave at the start of the method.
		// Currently, this is all except the 'if'-expression, since it can refer to the value of parameters
		PointcutExpression initialCheckTree = prunedTree.removeFromTree(IfPointcutExpression.class);
		
		NamingRegistry<RuntimePointcutExpression> expressionNames = new NamingRegistry<RuntimePointcutExpression>();
		
		IfThenElseStatement firstTest = null;
		if (initialCheckTree != null) {
			firstTest = getTest(initialCheckTree, expressionNames);
		}
		
		// Part two: inject the parameters
		// // There is only one parameter in catch clauses - the caught exception, so the parameter exposing pointcut doesn't really matter - we know the type and the name from the given parameter list
		Block secondPart = new Block();
		if (!parameters.isEmpty()) {
			FormalParameter fp = parameters.get(0);
			
			LocalVariableDeclarator parameterInjector = new LocalVariableDeclarator(fp.getTypeReference().clone());
			VariableDeclaration parameterInjectorDecl = new VariableDeclaration(fp.getName());					
			parameterInjector.add(parameterInjectorDecl);
			
			ClassCastExpression cast = new ClassCastExpression(fp.getTypeReference().clone(), new NamedTargetExpression(((CatchClause) element.parent()).getExceptionParameter().getName()));
			
			parameterInjectorDecl.setInitialization(cast);
			parameterInjector.add(parameterInjectorDecl);
				
			secondPart.addStatement(parameterInjector);
		}
				

		// Get the if-expressions
		PointcutExpression ifExprTree = prunedTree.getPrunedTree(IfPointcutExpression.class);
		if (ifExprTree != null) {
			IfThenElseStatement secondTest = getTest(ifExprTree, expressionNames);
			secondTest.setElseStatement(element.clone());
			secondPart.addBlock(addTest(ifExprTree, expressionNames, secondTest));
		}
		
		Block finalBody = new Block();
		
		// Now we determine the final method body
		if (firstTest != null) {
			firstTest.setElseStatement(secondPart);
			finalBody = addTest(initialCheckTree, expressionNames, firstTest);
		} else {
			finalBody = secondPart;
		}
				
		element.clear();
		element.addBlock(finalBody);
	}
	
	@Override
	protected Block getTerminateBody() {
		return originalBody.clone();
	}

	/**
	 * 	{@inheritDoc}
	 */
	public MatchResult<? extends PointcutExpression, ? extends Block> getMatchResult() {
		return (MatchResult<? extends PointcutExpression, ? extends Block>) super.getMatchResult();
	}
}

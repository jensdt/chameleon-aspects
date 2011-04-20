package chameleon.aspects.advice.runtimetransformation;

import java.util.ArrayList;
import java.util.List;

import chameleon.aspects.WeavingEncapsulator;
import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.member.simplename.operator.prefix.PrefixOperatorInvocation;
import chameleon.support.statement.IfThenElseStatement;
import chameleon.support.variable.LocalVariableDeclarator;

/**
 * 	See the Coordinator interface for documentation. This class implements some basic functionality
 * 
 * @author Jens
 *
 * @param <T>
 */
public abstract class AbstractCoordinator<T extends Element<?>> implements Coordinator<T> {

	/**
	 * 	The advice transformer used
	 * 
	 * 	FIXME: changed this type, rename the variables/getter/setter
	 */
	private RuntimeTransformationProvider adviceTransformationProvider;
	
	/**
	 * 	The matched joinpoint
	 */
	private MatchResult<?, ?> matchResult;
	
	private WeavingEncapsulator nextWeavingEncapsulator;
	private WeavingEncapsulator previousWeavingEncapsulator;
	
	/**
	 * 	Constructor
	 * 
	 * 	@param 	adviceTransformationProvider
	 * 			The used advice transformer
	 * 	@param 	matchResult
	 * 			The join point
	 */
	public AbstractCoordinator(RuntimeTransformationProvider adviceTransformationProvider, MatchResult<?, ?> matchResult, WeavingEncapsulator previousWeavingEncapsulator, WeavingEncapsulator nextWeavingEncapsulator) {
		this.adviceTransformationProvider = adviceTransformationProvider;
		this.matchResult = matchResult;
		this.nextWeavingEncapsulator = nextWeavingEncapsulator;
		this.previousWeavingEncapsulator = previousWeavingEncapsulator;
	}
	
	/**
	 * 	Get the join point
	 * 
	 * 	@return	the join point
	 */
	public MatchResult<? extends PointcutExpression, ? extends Element> getMatchResult() {
		return matchResult;
	}
	
	/**
	 * 	Get a list of declarations for all runtime tests in a pointcut expression tree. 
	 * 
	 * 	For instance, a if(Logger.enabled) test could be: boolean $_randomname = Logger.enabled;
	 * 		
	 * 
	 * 	@param 	tree
	 * 			The pointcut expression tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 * 	@return	The list of statements
	 */
	protected List<Statement> getDeclarations(RuntimePointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		List<Statement> result = new ArrayList<Statement>();
		
		if (tree == null)
			return result;

		// Get the tree in list-form, with the leaves first (postorder)
		List<RuntimePointcutExpression> expressions = tree.toPostorderList();

		// Get their expressions and assign them to booleans
		for (RuntimePointcutExpression expression : expressions) {
			RuntimePointcutExpression<?> actualExpression = (RuntimePointcutExpression<?>) expression.origin();
			
			RuntimeExpressionProvider transformer = getAdviceTransformationProvider().getRuntimeTransformer(actualExpression);
			Expression runtimeCheck = transformer.getExpression(actualExpression, expressionNames);
			
			// Create a boolean to assign the result to
			LocalVariableDeclarator testDecl = new LocalVariableDeclarator(new BasicTypeReference("boolean"));
			VariableDeclaration test = new VariableDeclaration("_$" + expressionNames.getName(actualExpression));
			test.setInitialization(runtimeCheck);
			testDecl.add(test);
			
			result.add(testDecl);
		}
		
		return result;
	}
	
	
	/**
	 * 	Return the test corresponding to the items in the tree
	 * 
	 * 	@param 	tree
	 * 			The expression tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 */
	protected Block addTest(RuntimePointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		return addTest(tree, expressionNames, getTest(tree, expressionNames));
	}
	
	protected Block addTest(RuntimePointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames, IfThenElseStatement test) {
		Block body = new Block();
		
		// Insert all the selected runtime expression-statements
		List<Statement> statements = getDeclarations(tree, expressionNames);
		for (Statement st : statements)
			body.addStatement(st);
		
		body.addStatement(test);
		
		return body;
	}
	
	protected IfThenElseStatement getTest(RuntimePointcutExpression<?> tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		// Now, convert the actual expression to the right test - get the name of the root
		Expression completeTest = new NamedTargetExpression("_$" + expressionNames.getName((RuntimePointcutExpression<?>) tree.origin()));
		
		// Negate it, since we do the return of the original method if the expression returns false
		PrefixOperatorInvocation negation = new PrefixOperatorInvocation("!", completeTest);
		
		IfThenElseStatement ifStatement = new IfThenElseStatement(negation, getTerminateBody(), null);
		
		return ifStatement;
	}
	
	/**
	 * 	Get the body of the if-clause that determines a runtime check failed.
	 * 
	 * 	E.g. for method invocations, this executes the original method (instead of weaving)
	 * 
	 * 	@return	The body of the if clause
	 */
	protected abstract Block getTerminateBody();
	

	/**
	 * 	Return the advice transformer
	 * 
	 * 	@return	The advice transformer
	 */
	public RuntimeTransformationProvider getAdviceTransformationProvider() {
		return adviceTransformationProvider;
	}

	public WeavingEncapsulator getNextWeavingEncapsulator() {
		return nextWeavingEncapsulator;
	}
}

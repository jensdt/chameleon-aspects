package chameleon.aspects.advice.runtimetransformation;

import java.util.ArrayList;
import java.util.List;

import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.generic.PointcutExpressionAnd;
import chameleon.aspects.pointcut.expression.generic.PointcutExpressionNot;
import chameleon.aspects.pointcut.expression.generic.PointcutExpressionOr;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.statement.Block;
import chameleon.core.statement.Statement;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;
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
	
	/**
	 * 	Constructor
	 * 
	 * 	@param 	adviceTransformationProvider
	 * 			The used advice transformer
	 * 	@param 	matchResult
	 * 			The join point
	 */
	public AbstractCoordinator(RuntimeTransformationProvider adviceTransformationProvider, MatchResult<?, ?> matchResult) {
		this.adviceTransformationProvider = adviceTransformationProvider;
		this.matchResult = matchResult;
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
	protected List<Statement> getDeclarations(PointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		List<Statement> result = new ArrayList<Statement>();
		
		if (tree == null)
			return result;
		
		// Select all runtime pointcut expressions, doesn't matter which order
		List<RuntimePointcutExpression> expressions = tree.descendants(RuntimePointcutExpression.class);
		if (tree instanceof RuntimePointcutExpression)
			expressions.add((RuntimePointcutExpression) tree);
				
		// Get their expressions and assign them to booleans
		for (RuntimePointcutExpression expression : expressions) {
			RuntimePointcutExpression actualExpression = (RuntimePointcutExpression) expression.origin();
			
			if (!getAdviceTransformationProvider().supports(actualExpression))
				continue;
			
			RuntimeExpressionProvider transformer = getAdviceTransformationProvider().getRuntimeTransformer(actualExpression);
			Expression runtimeCheck = transformer.getExpression(actualExpression);
			
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
	protected Block addTest(PointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		return addTest(tree, expressionNames, getTest(tree, expressionNames));
	}
	
	protected Block addTest(PointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames, IfThenElseStatement test) {
		Block body = new Block();
		
		// Insert all the selected runtime expression-statements
		List<Statement> statements = getDeclarations(tree, expressionNames);
		for (Statement st : statements)
			body.addStatement(st);
		
		body.addStatement(test);
		
		return body;
	}
	
	protected IfThenElseStatement getTest(PointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		// Now, convert the actual expression to the right test
		Expression completeTest = convertToTest(tree, expressionNames);
		
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
	 * 	Convert the given pointcut expression to a test. Requires that declarations are already in place.
	 * 
	 * 	Eg: if (Logger.enabled) && (thisType(Integer) || thisType(Double) => (_$logger && (_$type1 || _$type2)
	 * 
	 * 	@param 	tree
	 * 			The pointcut expression tree to convert
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 * 	@return	The test
	 */
	protected Expression convertToTest(PointcutExpression tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		if (tree instanceof PointcutExpressionAnd)
			return convertToTest((PointcutExpressionAnd) tree, expressionNames);
		if (tree instanceof PointcutExpressionOr)
			return convertToTest((PointcutExpressionOr) tree, expressionNames);
		if (tree instanceof PointcutExpressionNot)
			return convertToTest((PointcutExpressionNot) tree, expressionNames);
		if (tree instanceof RuntimePointcutExpression)
			return convertToTest((RuntimePointcutExpression) tree, expressionNames);
		
		throw new RuntimeException();
	}
	
	/**
	 * 	Converts a not-pointcut expression to a test: negate the test contained in the sub expression
	 * 
	 * 	@param tree
	 * 			The pointcut tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 * 	@return	The test
	 */
	protected Expression convertToTest(PointcutExpressionNot tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		Expression sub = convertToTest(tree.expression(), expressionNames);
		
		PrefixOperatorInvocation test = new PrefixOperatorInvocation("!", sub);
		
		return test;
	}
	
	/**
	 * 	Converts an and-pointcut expression to a test: conjunct the tests contained in the sub expressions
	 * 
	 * 	@param tree
	 * 			The pointcut tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 * 	@return	The test
	 */
	protected Expression convertToTest(PointcutExpressionAnd tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		Expression left = convertToTest(tree.expression1(), expressionNames);
		Expression right = convertToTest(tree.expression2(), expressionNames);
		
		InfixOperatorInvocation test = new InfixOperatorInvocation("&&", left);
		test.addArgument(right);
		
		return test;
	}
	
	/**
	 * 	Converts an or-pointcut expression to a test: disjunct the tests contained in the sub expressions
	 * 
	 * 	@param tree
	 * 			The pointcut tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 * 	@return	The test
	 */
	protected Expression convertToTest(PointcutExpressionOr tree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		Expression left = convertToTest(tree.expression1(), expressionNames);
		Expression right = convertToTest(tree.expression2(), expressionNames);
		
		InfixOperatorInvocation test = new InfixOperatorInvocation("||", left);
		test.addArgument(right);
		
		return test;
	}
	
	
	/**
	 * 	Converts a runtime pointcut expression to a test: just refer to its declaration as boolean expression
	 * 
	 * 	@param tree
	 * 			The pointcut tree
	 * 	@param 	expressionNames
	 * 			The naming registry for expressions
	 * 	@return	The test
	 */
	protected Expression convertToTest(RuntimePointcutExpression initialCheckTree, NamingRegistry<RuntimePointcutExpression> expressionNames) {
		return new NamedTargetExpression("_$" + expressionNames.getName((RuntimePointcutExpression) initialCheckTree.origin()));
	}

	/**
	 * 	Return the advice transformer
	 * 
	 * 	@return	The advice transformer
	 */
	public RuntimeTransformationProvider getAdviceTransformationProvider() {
		return adviceTransformationProvider;
	}
}

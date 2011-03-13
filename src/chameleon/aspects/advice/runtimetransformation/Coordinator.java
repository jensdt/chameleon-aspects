package chameleon.aspects.advice.runtimetransformation;

import java.util.List;

import chameleon.aspects.advice.types.translation.AdviceTransformationProvider;
import chameleon.aspects.namingRegistry.NamingRegistry;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.IfPointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.method.Method;
import chameleon.core.method.RegularImplementation;
import chameleon.core.statement.Block;
import chameleon.core.variable.VariableDeclaration;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.variable.LocalVariableDeclarator;

public class Coordinator<T extends Element> {

	private AdviceTransformationProvider adviceTransformationProvider;
	private MatchResult matchResult;
	
	public Coordinator(AdviceTransformationProvider adviceTransformationProvider, MatchResult matchResult) {
		this.adviceTransformationProvider = adviceTransformationProvider;
		this.matchResult = matchResult;
	}

	public void transform(T element) {
		if (element == null)
			return;
		
		// First, get all the runtime pointcut expressions but maintain the structure (and/or/...)
		PointcutExpression prunedTree = matchResult.getExpression().getPrunedTree(RuntimePointcutExpression.class);
		
		// Now, select all pointcut expressions we can weave at the start of the method.
		// Currently, this is all except the 'if'-expression, since it can refer to the value of parameters
		PointcutExpression initialCheckTree = prunedTree.removeFromTree(IfPointcutExpression.class);
		
		// Select all runtime pointcut expressions, doesn't matter which order
		List<RuntimePointcutExpression> expressions = initialCheckTree.descendants(RuntimePointcutExpression.class);
		
		NamingRegistry<RuntimePointcutExpression> expressionNames = new NamingRegistry<RuntimePointcutExpression>();
		Block finalBody = new Block();
		
		for (RuntimePointcutExpression expression : expressions) {
			if (!adviceTransformationProvider.canTransform(expression))
				continue;
			
			RuntimeTransformer transformer = adviceTransformationProvider.getRuntimeTransformer(expression);
			Expression runtimeCheck = transformer.getExpression(expression);
			
			// Create a boolean to assign the result to
			LocalVariableDeclarator testDecl = new LocalVariableDeclarator(new BasicTypeReference("boolean"));
			VariableDeclaration test = new VariableDeclaration("_$" + expressionNames.getName(expression));
			test.setInitialization(runtimeCheck);
			testDecl.add(test);
			
			finalBody.addStatement(testDecl);
		}
		
		((Method) element).setImplementation(new RegularImplementation(finalBody));
	}
}

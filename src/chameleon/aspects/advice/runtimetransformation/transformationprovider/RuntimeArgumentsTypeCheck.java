package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import java.util.List;

import jnome.core.expression.ArrayAccessExpression;
import jnome.core.language.Java;
import chameleon.aspects.pointcut.expression.runtime.ArgsPointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.RuntimePointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.FilledArrayIndex;
import chameleon.support.expression.InstanceofExpression;
import chameleon.support.expression.RegularLiteral;
import chameleon.support.member.simplename.operator.infix.InfixOperatorInvocation;

/**
 * 	Performs a runtime type check of arguments
 * 
 * 	FIXME: correct indexing
 * 
 * 	@author Jens
 *
 */
public class RuntimeArgumentsTypeCheck implements RuntimeExpressionProvider {
	
	/**
	 * 	Reference to the parameter containing the arguments
	 */
	private NamedTargetExpression argumentReference;
	
	/**
	 * 	Constructor
	 * 
	 * 	@param 	argumentReference
	 * 			Reference to the parameter containing the arguments
	 */
	public RuntimeArgumentsTypeCheck(NamedTargetExpression argumentReference) {
		this.argumentReference = argumentReference;
	}
	
	/**
	 *  {@inheritDoc}
	 *  
	 *  The expression is always of the following form: first a check for the correct count (so we get short-circuited if this isn't correct and avoid an IndexOutOfBounds).
	 *  Then a type check for each parameter. Primitive parameter types are always boxed to perform the check.
	 *  
	 *  e.g. arguments(String, int) => (args.length == 2) && (args[0] instanceof String) && (args[1] instanceof Integer)
	 *  
	 *  
	 */
	@Override
	public Expression<?> getExpression(RuntimePointcutExpression<?> expr) {
		if (!(expr instanceof ArgsPointcutExpression))
			return null;
		
		ArgsPointcutExpression<?> argumentsExpression = (ArgsPointcutExpression<?>) expr;
		
		// First, add a check if the number of parameters matches
		NamedTargetExpression parLength = new NamedTargetExpression("length", argumentReference.clone());
		InfixOperatorInvocation equals = new InfixOperatorInvocation("==", parLength);
		equals.addArgument(new RegularLiteral(new BasicTypeReference<BasicTypeReference<?>>("int"), Integer.toString(argumentsExpression.parameters().size())));
		
		InfixOperatorInvocation fullTest = equals;
		
		// Add a check for each parameter defined in the Arguments expression
		int i = 0;
		for (NamedTargetExpression parameter : (List<NamedTargetExpression>) argumentsExpression.parameters()) {

			// Access the correct element of the array
			ArrayAccessExpression arrayAccess = new ArrayAccessExpression(argumentReference.clone());
			arrayAccess.addIndex(new FilledArrayIndex(new RegularLiteral(new BasicTypeReference<BasicTypeReference<?>>("int"), Integer.toString(i++))));

			TypeReference<?> typeToTest = null;
			Java java = parameter.language(Java.class);
			try {
				if (parameter.getType().isTrue(java.property("primitive")))
					typeToTest = new BasicTypeReference<BasicTypeReference<?>>(java.box(parameter.getType()).getFullyQualifiedName());
				else
					typeToTest = new BasicTypeReference<BasicTypeReference<?>>(parameter.getType().getFullyQualifiedName());
			} catch (LookupException e) {
				System.out.println("Lookupexception while boxing");
			}
			
			// Create the instanceof
			InstanceofExpression test = new InstanceofExpression(arrayAccess, typeToTest);
			
			fullTest = new InfixOperatorInvocation("&&", fullTest.clone());
			fullTest.addArgument(test);
		}
		
		return fullTest;
	}
}
package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;
import chameleon.aspects.pointcut.expression.runtime.ArgsPointcutExpression;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.oo.type.TypeReference;
import chameleon.support.expression.InstanceofExpression;

public class RuntimeSingleArgumentTypeCheck extends RuntimeArgumentsTypeCheck {

	public RuntimeSingleArgumentTypeCheck(NamedTargetExpression argumentReference) {
		super(argumentReference);
	}

	/**
	 *  {@inheritDoc}
	 *  
	 *  Since there is only a single argument, just check that
	 *  
	 */
	@Override
	public Expression<?> getExpression(RuntimePointcutExpression<?> expr) {
		if (!(expr instanceof ArgsPointcutExpression))
			return null;
		
		ArgsPointcutExpression<?> argumentsExpression = (ArgsPointcutExpression<?>) expr;
		NamedTargetExpression parameter = argumentsExpression.parameters().get(0);
		TypeReference<?> typeToTest = getTypeToTest(parameter);
		
		// Create the instanceof
		InstanceofExpression test = new InstanceofExpression(getArgumentReference(), typeToTest);

		return test;
	}
}

package chameleon.aspects.advice.runtimetransformation;

import chameleon.aspects.advice.runtimetransformation.transformationprovider.RuntimeExpressionProvider;
import chameleon.aspects.pointcut.expression.generic.RuntimePointcutExpression;

public interface RuntimeTransformationProvider {
	public boolean supports(RuntimePointcutExpression pointcutExpression);
	public RuntimeExpressionProvider getRuntimeTransformer(RuntimePointcutExpression pointcutExpression);	 
	//FIXME: move coordinator here... mabye
}
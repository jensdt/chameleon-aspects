package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import java.util.List;

import chameleon.aspects.pointcut.expression.dynamicexpression.ParameterExposurePointcutExpression;
import chameleon.core.variable.FormalParameter;
import chameleon.support.variable.LocalVariableDeclarator;

public interface RuntimeParameterExposureProvider<T extends ParameterExposurePointcutExpression<?>> {
	public List<LocalVariableDeclarator> getParameterExposureDeclaration(T expression, FormalParameter fp);
}

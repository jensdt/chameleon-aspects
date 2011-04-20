package chameleon.aspects.advice.runtimetransformation.transformationprovider;

import chameleon.core.variable.FormalParameter;
import chameleon.support.variable.LocalVariableDeclarator;

public interface RuntimeParameterExposureProvider<E> {
	public LocalVariableDeclarator getParameterExposureDeclaration(E expression, FormalParameter fp);
}

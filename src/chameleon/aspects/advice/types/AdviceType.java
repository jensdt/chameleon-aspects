package chameleon.aspects.advice.types;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public interface AdviceType<T, U extends Element> {
	public T getAdviceTransformationResult(CompilationUnit compilationUnit, MatchResult<? extends PointcutExpression, ? extends U> joinpoint)  throws LookupException;
}

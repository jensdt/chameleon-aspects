package chameleon.aspects.advice.types.weaving;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.generic.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public interface AdviceWeaveResultProvider<T extends Element, U> {
	public U getWeaveResult(CompilationUnit compilationUnit, Advice advice, MatchResult<? extends PointcutExpression, ? extends T> joinpoint) throws LookupException;
}

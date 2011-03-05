package chameleon.aspects.advice.types.translation;

import chameleon.aspects.advice.Advice;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.lookup.LookupException;

public interface AdviceTranslationProvider {
	public void transform(CompilationUnit compilationUnit, Advice advice)  throws LookupException;
}

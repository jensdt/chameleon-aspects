package chameleon.aspects.pointcut.expression.dynamicexpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.expression.AbstractPointcutExpression;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public abstract class AbstractDynamicPointcutExpression<E extends AbstractDynamicPointcutExpression<E>> extends AbstractPointcutExpression<E> {
	/**
	 *	{@inheritDoc}
	 */
	@Override
	public List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		List<MatchResult> results = new ArrayList<MatchResult>();
		
		for (Element mi : compilationUnit.descendants())
			results.add(new MatchResult<Element>(this, mi));

		return results; 
	}
}
package chameleon.aspects.pointcut.expression.staticexpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import chameleon.aspects.pointcut.expression.AbstractPointcutExpression;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.variable.FormalParameter;

public abstract class AbstractStaticPointcutExpression<E extends AbstractStaticPointcutExpression<E>> extends AbstractPointcutExpression<E> implements StaticPointcutExpression<E> {
	/**
	 *	{@inheritDoc}
	 */
	@Override
	public final List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		List<MatchResult> results = new ArrayList<MatchResult>();
		
		for (Class c : (Set<Class<? extends Element>>) supportedJoinpoints()) {
			List<Element> descendants = compilationUnit.descendants(c);
			for (Element mi : descendants) {
				try {
					MatchResult match = matches(mi);
				
					if (match.isMatch())
						results.add(match);
				} catch (LookupException e) {
					
				}
			}
		}
		return results; 
	}	
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		MatchResult matches = matches(joinpoint);
		
		if (matches.isMatch())
			return MatchResult.noMatch();
		else
			return new MatchResult(this, joinpoint);
	}
}
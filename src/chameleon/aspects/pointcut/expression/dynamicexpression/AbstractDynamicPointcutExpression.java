package chameleon.aspects.pointcut.expression.dynamicexpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
	public final List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		List<MatchResult> results = new ArrayList<MatchResult>();
		
		for (Class c : (Set<Class<? extends Element>>) supportedJoinpoints()) {
			List<Element> descendants = compilationUnit.descendants(c);
			for (Element mi : descendants) {
				results.add(new MatchResult<AbstractDynamicPointcutExpression<E>, Element>(this, mi));
			}
		}
		return results; 
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public Set<Class<? extends Element>> supportedJoinpoints() {
		return Collections.<Class<? extends Element>>singleton(Element.class);
	}
}

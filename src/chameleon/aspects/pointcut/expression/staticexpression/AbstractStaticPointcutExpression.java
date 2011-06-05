package chameleon.aspects.pointcut.expression.staticexpression;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.predicate.SafePredicate;

import chameleon.aspects.pointcut.expression.AbstractPointcutExpression;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;

public abstract class AbstractStaticPointcutExpression<E extends AbstractStaticPointcutExpression<E>> extends AbstractPointcutExpression<E> implements StaticPointcutExpression<E> {
	/**
	 *	{@inheritDoc}
	 */
	@Override
	public final List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		List<MatchResult> results = new ArrayList<MatchResult>();
		
		List<Element> descendants = compilationUnit.descendants(new SafePredicate<Element>() {
			@Override
			public boolean eval(Element object) {
				return isSupported(object.getClass());
			}
		});
		
		for (Element mi : descendants) {
			try {
				MatchResult match = matches(mi);
			
				if (match.isMatch())
					results.add(match);
			} catch (LookupException e) {
				e.printStackTrace();
			}
		}

		return results; 
	}
}
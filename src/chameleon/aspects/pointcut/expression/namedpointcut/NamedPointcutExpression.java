package chameleon.aspects.pointcut.expression.namedpointcut;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.PointcutReference;
import chameleon.aspects.pointcut.expression.AbstractPointcutExpression;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.aspects.pointcut.expression.dynamicexpression.ParameterExposurePointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.util.Util;

public class NamedPointcutExpression<E extends NamedPointcutExpression<E>> extends AbstractPointcutExpression<E> implements CrossReference<E, Pointcut> {
	
	private SingleAssociation<NamedPointcutExpression<E>, PointcutReference> _pointcutReference = new SingleAssociation<NamedPointcutExpression<E>, PointcutReference>(this);
	
	public void setPointcutReference(PointcutReference ref) {
		setAsParent(_pointcutReference, ref);
	}
	
	public PointcutReference<?> pointcutReference() {
		return _pointcutReference.getOtherEnd();
	}

	@Override
	public List<? extends Element> children() {
		return Util.createNonNullList(pointcutReference());
	}


	@Override
	public E clone() {
		NamedPointcutExpression clone = new NamedPointcutExpression<E>();
		
		if (pointcutReference() != null)
			clone.setPointcutReference(pointcutReference().clone());
		
		return (E) clone;
	}

	@Override
	public Pointcut getElement() throws LookupException {
		if (pointcutReference() == null)
			return null;
		
		return pointcutReference().getElement();
	}

	@Override
	public Declaration getDeclarator() throws LookupException {
		if (pointcutReference() == null)
			return null;
		
		return pointcutReference().getDeclarator();
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		throw new ChameleonProgrammerException("Named pointcut expression not expanded before operations!");
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public boolean hasParameter(FormalParameter fp) {
		return pointcutReference().hasParameter(fp);
	}
	
	/**
	 *  {@inheritDoc}
	 */
	@Override
	public PointcutExpression<?> expand() {
		try {
			/* We replace this reference to a pointcut, by the pointcut expression of the pointcut. However, we must take into account that
			 parameters have to be renamed.
			 	e.g.
			 	
			 		Pointcut a(Foo param) : thisType(param);
			 		Pointcut b(Foo realParam): call(void *.doSomething()) && a(realParam)
			
			
					=> Pointcut b(Foo realParam): call(void *.doSomething()) && thisType(realParam)
			
			*/
			PointcutExpression<?> pointcutExpression = ((PointcutExpression<?>) getElement().expression()).expand();
			
			if (!pointcutReference().getActualParameters().isEmpty()) {
				// Map is from->to
				Map<String, String> parameterNamesMap = new HashMap<String, String>();
				
				Iterator<FormalParameter> pointcutParameters = getElement().parameters().iterator();
				Iterator<Expression> referenceParameters = pointcutReference().getActualParameters().iterator();
				
				while (pointcutParameters.hasNext() && referenceParameters.hasNext()) {
					Expression _nextReferenceParam  = referenceParameters.next();
					
					if (!(_nextReferenceParam instanceof NamedTargetExpression))
						throw new ChameleonProgrammerException("Pointcut reference has a parameter that isn't a named target expr");
					
					NamedTargetExpression nextReferenceParam = (NamedTargetExpression) _nextReferenceParam;
					FormalParameter nextPointcutParam = pointcutParameters.next();
					
					parameterNamesMap.put(nextPointcutParam.getName(), nextReferenceParam.name());
				}
				
				// We know there are parameters, so we know the pointcut expression must be a ParameterExposurePointcutExpression, so the cast is no problem
				((ParameterExposurePointcutExpression<?>) pointcutExpression).renameParameters(parameterNamesMap);
			}
			
			return pointcutExpression;
						
		} catch (LookupException e) {
			// Should not be able to occur
			e.printStackTrace();
			
			return null;
		}
	}
}
package chameleon.aspects.pointcut.expression.staticexpression.catchclause;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.aspects.pointcut.expression.SubtypeMarker;
import chameleon.aspects.pointcut.expression.staticexpression.AbstractStaticPointcutExpression;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.oo.type.TypeReference;
import chameleon.support.statement.CatchClause;
import chameleon.util.Util;

public class CatchClausePointcutExpression<E extends CatchClausePointcutExpression<E>> extends AbstractStaticPointcutExpression<E> {
	
	private SingleAssociation<CatchClausePointcutExpression<E>, TypeReference<?>> _exceptionType = new SingleAssociation<CatchClausePointcutExpression<E>, TypeReference<?>>(this);
	
	// TODO: check wanted behavior for subtypes
	private SingleAssociation<CatchClausePointcutExpression<E>, SubtypeMarker<?>> _subtypeMarker = new SingleAssociation<CatchClausePointcutExpression<E>, SubtypeMarker<?>>(this);
	
	public TypeReference<?> exceptionType() {
		return _exceptionType.getOtherEnd();
	}
	
	public void setExceptionType(TypeReference<?> exceptionType) {
		setAsParent(_exceptionType, exceptionType);
	}
	
	public SubtypeMarker<?> subtypeMarker() {
		return _subtypeMarker.getOtherEnd();
	}
	
	public boolean hasSubtypeMarker() {
		return subtypeMarker() != null;
	}
	
	public void setSubtypeMarker(SubtypeMarker<?> marker) {
		setAsParent(_subtypeMarker, marker);
	}
	
	@Override
	public MatchResult matches(Element element) throws LookupException {	
		if (!(element.parent() instanceof CatchClause))
			return MatchResult.noMatch();
		
		if (((CatchClause) element.parent()).statement() != element)
			return MatchResult.noMatch();
				
		if (!((CatchClause) element.parent()).getExceptionParameter().getType().assignableTo(exceptionType().getType()))
			return MatchResult.noMatch();
		
		return new MatchResult(this, element);
	}

	@Override
	public Set<Class<? extends Element>> supportedJoinpoints() {
		Set<Class<? extends Element>> result = new HashSet<Class<? extends Element>>();
		result.add(Block.class);
		return result;
	}

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(exceptionType(), result);
		Util.addNonNull(subtypeMarker(), result);
		
		return result;
	}

	@Override
	public E clone() {
		CatchClausePointcutExpression clone = new CatchClausePointcutExpression();
		
		if (exceptionType() != null)
			clone.setExceptionType(exceptionType().clone());
		
		if (subtypeMarker() != null)
			clone.setSubtypeMarker(subtypeMarker().clone());
		
		return (E) clone;
	}
}
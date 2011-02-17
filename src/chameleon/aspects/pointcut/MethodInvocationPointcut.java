package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.compilationunit.CompilationUnit;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.lookup.LookupException;
import chameleon.core.reference.CrossReference;
import chameleon.core.scope.Scope;
import chameleon.exception.ModelException;

public class MethodInvocationPointcut<E extends MethodInvocationPointcut<E>> extends Pointcut<E> {

	public MethodInvocationPointcut() {
		super();
	}
	
	public MethodInvocationPointcut(SimpleNameDeclarationWithParametersHeader header) {
		super(header);
	}
	
	public MethodInvocationPointcut(SimpleNameDeclarationWithParametersHeader header, PointcutExpression expression) {
		super(header, expression);
	}
	
	
	@Override
	public List<MatchResult> joinpoints(CompilationUnit compilationUnit) throws LookupException {
		List<MatchResult> results = new ArrayList<MatchResult>();
		
		List<MethodInvocation> descendants = compilationUnit.descendants(MethodInvocation.class);
		for (MethodInvocation mi : descendants) {
			try {
				MatchResult match = expression().matches(mi);
			
				if (match.isMatch())
					results.add(match);
			} catch (LookupException e) {
				// TODO: this is thrown because the aspect class isn't in the model yet. Fix this!
			}
		}
		
		return results; 
	}
	

	@Override
	public E clone() {
		MethodInvocationPointcut clone = new MethodInvocationPointcut();
		clone.setHeader((SimpleNameDeclarationWithParametersHeader) header().clone());
		clone.setSignature(signature().clone());
		clone.setExpression(expression().clone());
		
		return (E) clone;
	}

	@Override
	public Scope scope() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}
}

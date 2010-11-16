package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rejuse.predicate.UnsafePredicate;

import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.MethodHeader;
import chameleon.core.reference.CrossReference;
import chameleon.core.scope.Scope;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ModelException;

public class CrossReferencePointcut<E extends CrossReferencePointcut<E>> extends Pointcut<E> {

	public CrossReferencePointcut() {
		super();
	}
	
	public CrossReferencePointcut(PointcutHeader header) {
		super(header);
	}
	
	public CrossReferencePointcut(PointcutHeader header, PointcutExpression expression) {
		super(header, expression);
	}
	
	
	@Override
	public List<? extends CrossReference> joinpoints() throws LookupException {
		return language().defaultNamespace().descendants(CrossReference.class,
				new UnsafePredicate<CrossReference, LookupException>() {

					@Override
					public boolean eval(final CrossReference cr) throws LookupException {
						return expression() == null || expression().matches(cr);
					}

				}

		);
//		return language().defaultNamespace().descendants(CrossReference.class, 
//				new UnsafePredicate<CrossReference, LookupException>() {
//
//					@Override
//					public boolean eval(final CrossReference cr) throws LookupException {
//						return new UnsafePredicate<MethodHeader, LookupException>() {
//
//							@Override
//							public boolean eval(MethodHeader hs)
//									throws LookupException {
//								return cr != null && cr.getElement().signature().sameAs(hs.signature());
//							}
//							
//						}.exists(joinpointNames);
//					}
//
//			
//				}
//		
//		);
	}
	

	@Override
	public E clone() {
		CrossReferencePointcut clone = new CrossReferencePointcut();
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

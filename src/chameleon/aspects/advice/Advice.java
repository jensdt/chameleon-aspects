package chameleon.aspects.advice;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.aspects.Aspect;
import chameleon.aspects.pointcut.Pointcut;
import chameleon.aspects.pointcut.PointcutReference;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableContainer;
import chameleon.util.Util;

public class Advice<E extends Advice<E>> extends NamespaceElementImpl<E, Element> implements VariableContainer<E, Element> {

	public Advice() {
		
	}

	
	private OrderedMultiAssociation<Advice<E>, FormalParameter> _parameters = new OrderedMultiAssociation<Advice<E>, FormalParameter>(this);
	private SingleAssociation<Advice<E>, Element> _body = new SingleAssociation<Advice<E>, Element>(this);
	private SingleAssociation<Advice<E>, PointcutReference> _pointcutReference = new SingleAssociation<Advice<E>, PointcutReference>(this);
	
	public Element body() {
		return _body.getOtherEnd();
	}
	
	public void setBody(Element element) {
		setAsParent(_body, element);
	}
	
	public List<FormalParameter> formalParameters() {
		return _parameters.getOtherEnds();
	}
	
	public void addFormalParameter(FormalParameter param) {
		setAsParent(_parameters, param);
	}
	
	public void addFormalParameters(List<FormalParameter> params) {
		if (params == null)
			return;
		
		for (FormalParameter p : params)
			addFormalParameter(p);
	}
	
	@Override
	public LookupStrategy lexicalLookupStrategy(Element element)
			throws LookupException {
		if (pointcutReference().equals(element) || body().equals(element)) {
			if (_lexical == null) {
				_lexical = language().lookupFactory()
						.createLexicalLookupStrategy(localLookupStrategy(),
								this);
			}
			return _lexical;			
		} else {
			return parent().lexicalLookupStrategy(this);
		}
	}

	public LookupStrategy localLookupStrategy() {
		if (_local == null) {
			_local = language().lookupFactory()
					.createTargetLookupStrategy(this);
		}
		return _local;
	}
	
	private LookupStrategy _local;

	private LookupStrategy _lexical;
	
	/**
	 * 	Get the Aspect this Advice belongs to
	 */
	public Aspect aspect() {
		return (Aspect) parentLink().getOtherEnd();
	}
	
	private Pointcut pointcut;
	
	/**
	 * 	Get the pointcut that this Advice applies to
	 */
	public Pointcut pointcut() {
		try {
			return pointcutReference().getElement();
		} catch (LookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}

	private PointcutReference pointcutReference() {
		return _pointcutReference.getOtherEnd();
	}

	public void setPointcutReference(PointcutReference pointcutref)  {
		setAsParent(_pointcutReference, pointcutref);
	}
	

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(body(), result);
		Util.addNonNull(pointcutReference(), result);
		result.addAll(formalParameters());
		
		return result;
	}

	@Override
	public E clone() {
		Advice clone = new Advice();
		clone.setPointcutReference(pointcutReference().clone());
		clone.setBody(body().clone());
		
		for (FormalParameter p : formalParameters())
			clone.addFormalParameter(p.clone());
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
			
		return result;
	}

	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		return formalParameters();
	}

	@Override
	public List<? extends Declaration> locallyDeclaredDeclarations()
			throws LookupException {
		return declarations();
	}

	@Override
	public <D extends Declaration> List<D> declarations(
			DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

	@Override
	public NamespaceElement variableScopeElement() {
		// TODO Auto-generated method stub
		return null;
	}
}

package chameleon.aspects.advice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableContainer;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class Advice<E extends Advice<E>> extends NamespaceElementImpl<E> implements VariableContainer<E> {

	public Advice(AdviceTypeEnum type, TypeReference returnType) {
		setType(type);

		if (returnType == null)
			setReturnType(new BasicTypeReference("void"));
		else
			setReturnType(returnType);
	}
	
	private AdviceTypeEnum type;
	private OrderedMultiAssociation<Advice<E>, FormalParameter> _parameters = new OrderedMultiAssociation<Advice<E>, FormalParameter>(this);
	private SingleAssociation<Advice<E>, Element> _body = new SingleAssociation<Advice<E>, Element>(this);
	private SingleAssociation<Advice<E>, PointcutReference> _pointcutReference = new SingleAssociation<Advice<E>, PointcutReference>(this);
	private SingleAssociation<Advice<E>, TypeReference> _returnType = new SingleAssociation<Advice<E>, TypeReference>(this);
	
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

	public PointcutReference pointcutReference() {
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
		Util.addNonNull(returnType(), result);
		result.addAll(formalParameters());
		
		return result;
	}

	@Override
	public E clone() {
		Advice clone = new Advice(type(), returnType().clone());
		clone.setPointcutReference(pointcutReference().clone());
		clone.setBody(body().clone());
		
		for (FormalParameter p : formalParameters())
			clone.addFormalParameter(p.clone());
		
		return (E) clone;
	}
	
	private List<FormalParameter> unresolvedParameters() {
		List<FormalParameter> unresolved = new ArrayList<FormalParameter>();
		
		for (FormalParameter fp : (List<FormalParameter>) formalParameters())
			if (!pointcutReference().hasParameter(fp))
				unresolved.add(fp);
		
		return unresolved;
	}
	
	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
//		
//		TODO: move this to advice
//		try {
//			if (!returnType().getType().getFullyQualifiedName().equals("void") && type() != AdviceType.AROUND)
//				result = result.and(new BasicProblem(this, "No return type allowed for " + type() + " advice"));
//				
//			
//		} catch (LookupException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		List<FormalParameter> unresolved = unresolvedParameters();
		if (!unresolved.isEmpty()) {
			
			StringBuffer unresolvedList = new StringBuffer();
			Iterator<FormalParameter> it = unresolved.iterator();
			unresolvedList.append(it.next().getName());
			
			while (it.hasNext()) {
				unresolvedList.append(", ");
				unresolvedList.append(it.next().getName());
			}
			
			result = result.and(new BasicProblem(this, "The following parameters cannot be resolved: " + unresolvedList));
		}
		
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

	public AdviceTypeEnum type() {
		return type;
	}

	public void setType(AdviceTypeEnum type) {
		this.type = type;
	}
	
	public TypeReference returnType() {
		return _returnType.getOtherEnd();
	}
	
	public void setReturnType(TypeReference returnType) {
		setAsParent(_returnType, returnType);
	}
}

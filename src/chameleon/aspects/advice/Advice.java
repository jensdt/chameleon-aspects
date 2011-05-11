package chameleon.aspects.advice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;
import org.rejuse.property.Property;
import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertySet;

import chameleon.aspects.Aspect;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.modifier.ElementWithModifiers;
import chameleon.core.modifier.Modifier;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.statement.Block;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableContainer;
import chameleon.exception.ModelException;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class Advice<E extends Advice<E>> extends NamespaceElementImpl<E>
		implements VariableContainer<E>, ElementWithModifiers<E> {

	public Advice(TypeReference returnType) {
		setReturnType(returnType);
	}

	private OrderedMultiAssociation<Advice<E>, FormalParameter> _parameters = new OrderedMultiAssociation<Advice<E>, FormalParameter>(this);
	private SingleAssociation<Advice<E>, Block> _body = new SingleAssociation<Advice<E>, Block>(this);
	private SingleAssociation<Advice<E>, PointcutExpression> _pointcutExpression = new SingleAssociation<Advice<E>, PointcutExpression>(this);
	private SingleAssociation<Advice<E>, TypeReference> _returnType = new SingleAssociation<Advice<E>, TypeReference>(this);

	public Block body() {
		return _body.getOtherEnd();
	}

	public void setBody(Block element) {
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
		if ((pointcutExpression() != null && pointcutExpression().equals(element)) || (body() != null && body().equals(element))) {
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
	 * Get the Aspect this Advice belongs to
	 */
	public Aspect aspect() {
		return (Aspect) parent();
	}
	
	public PointcutExpression getExpandedPointcutExpression() {
		PointcutExpression<?> expr = pointcutExpression();
		setPointcutExpression(expr.expand());
		
		return pointcutExpression();
	}

	private PointcutExpression pointcutExpression() {
		return _pointcutExpression.getOtherEnd();
	}

	public void setPointcutExpression(PointcutExpression<?> pointcutref) {
		setAsParent(_pointcutExpression, pointcutref);
	}

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();

		Util.addNonNull(body(), result);
		Util.addNonNull(pointcutExpression(), result);
		Util.addNonNull(returnType(), result);
		result.addAll(formalParameters());
		result.addAll(modifiers());

		return result;
	}

	@Override
	public E clone() {
		TypeReference returnTypeClone = null;
		if (returnType() != null)
			returnTypeClone = returnType().clone();

		Advice clone = new Advice(returnTypeClone);
		clone.setPointcutExpression((PointcutExpression) pointcutExpression().clone());
		clone.setBody(body().clone());

		for (FormalParameter p : formalParameters())
			clone.addFormalParameter(p.clone());
		
		for (Modifier m : modifiers())
			clone.addModifier(m.clone());

		return (E) clone;
	}

	private List<FormalParameter> unresolvedParameters() {
		List<FormalParameter> unresolved = new ArrayList<FormalParameter>();

		for (FormalParameter fp : (List<FormalParameter>) formalParameters())
			if (!pointcutExpression().hasParameter(fp))
				unresolved.add(fp);

		return unresolved;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();

		List<FormalParameter> unresolved = unresolvedParameters();
		if (!unresolved.isEmpty()) {

			StringBuffer unresolvedList = new StringBuffer();
			Iterator<FormalParameter> it = unresolved.iterator();
			unresolvedList.append(it.next().getName());

			while (it.hasNext()) {
				unresolvedList.append(", ");
				unresolvedList.append(it.next().getName());
			}

			result = result.and(new BasicProblem(this,
					"The following parameters cannot be resolved: "
							+ unresolvedList));
		}

		return result;
	}

	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		List<? extends Declaration> declarations =  formalParameters();
		
		for (Modifier m : modifiers()) {
			if (m instanceof VariableContainer)
				declarations.addAll(((VariableContainer) m).declarations());
		}
		
		return declarations;
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
		// TODO Auto-generated method stub - probably the body
		return null;
	}

	protected TypeReference returnType() {
		return _returnType.getOtherEnd();
	}

	public TypeReference actualReturnType() {
		if (returnType() == null)
			return new BasicTypeReference("void");
		else
			return returnType();
	}

	public void setReturnType(TypeReference returnType) {
		setAsParent(_returnType, returnType);
	}

	private OrderedMultiAssociation<Advice<E>, Modifier> _modifiers = new OrderedMultiAssociation<Advice<E>, Modifier>(
			this);

	@Override
	public List<Modifier> modifiers() {
		return _modifiers.getOtherEnds();
	}

	@Override
	public void addModifier(Modifier modifier) {
		if ((modifier != null) && (!_modifiers.contains(modifier.parentLink()))) {
			_modifiers.add(modifier.parentLink());
		}
	}

	@Override
	public void removeModifier(Modifier modifier) {
		_modifiers.remove(modifier.parentLink());
	}

	@Override
	public void addModifiers(List<Modifier> modifiers) {
		if (modifiers == null)
			return;

		for (Modifier modifier : modifiers)
			addModifier(modifier);

	}

	@Override
	public List<Modifier> modifiers(PropertyMutex mutex) throws ModelException {
		Property property = property(mutex);
		List<Modifier> result = new ArrayList<Modifier>();
		for (Modifier mod : modifiers()) {
			if (mod.impliesTrue(property)) {
				result.add(mod);
			}
		}
		return result;
	}

	@Override
	public List<Modifier> modifiers(Property property) throws ModelException {
	  	List<Modifier> result = new ArrayList<Modifier>();
	  	for(Modifier mod: modifiers()) {
	  		if(mod.impliesTrue(property)) {
	  			result.add(mod);
	  		}
	  	}
	  	return result;
	 }
	
	@Override
	public PropertySet<Element, ChameleonProperty> declaredProperties() {
		PropertySet<Element, ChameleonProperty> result = new PropertySet<Element, ChameleonProperty>();
		for (Modifier modifier : modifiers()) {
			result.addAll(modifier.impliedProperties());
		}
		return result;
	}
}

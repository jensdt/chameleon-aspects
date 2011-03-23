package chameleon.aspects.advice.types;

import java.util.List;

import org.rejuse.association.SingleAssociation;
import org.rejuse.property.PropertySet;

import chameleon.aspects.advice.properties.ThrowingProperty;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.modifier.Modifier;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableContainer;
import chameleon.oo.language.ObjectOrientedLanguage;
import chameleon.util.Util;

public class Throwing<E extends Throwing<E>> extends ModifierImpl<E> implements Modifier<E>, VariableContainer<E> {

	private SingleAssociation<Throwing<E>, FormalParameter> _exceptionParameter = new SingleAssociation<Throwing<E>, FormalParameter>(this);
	
	public FormalParameter exceptionParameter() {
		return _exceptionParameter.getOtherEnd();
	}
	
	public void setExceptionParameter(FormalParameter exceptionParameter) {
		setAsParent(_exceptionParameter, exceptionParameter);
	}
	
	public boolean hasExceptionParameter() {
		return exceptionParameter() != null;
	}
	
	@Override
	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language().property(ThrowingProperty.ID));
	}

	@Override
	public E clone() {
		FormalParameter exceptionParameterClone = null;
		
		if (hasExceptionParameter())
			exceptionParameterClone = exceptionParameter().clone();
		
		Throwing<E> clone = new Throwing<E>();
		clone.setExceptionParameter(exceptionParameterClone);
		
		return (E) clone;
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public List<? extends Element> children() {
		List<? extends Element> children = super.children();
		Util.addNonNull(exceptionParameter(), children);
		return children;
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = super.verifySelf();
		
		try {
			if (hasExceptionParameter() && !language(ObjectOrientedLanguage.class).isException(exceptionParameter().getType()))
				result = result.and(new BasicProblem(this, "Parameter must be a throwable"));
		} catch (LookupException e) {
			result = result.and(new BasicProblem(this, "Could not determine the parameter type"));
		}
		
		return result;
	}

	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		return Util.createNonNullList(exceptionParameter());
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

package chameleon.aspects.advice.types;

import java.util.List;

import org.rejuse.association.SingleAssociation;
import org.rejuse.property.PropertySet;

import chameleon.aspects.advice.properties.ReturningProperty;
import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.modifier.Modifier;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.namespace.NamespaceElement;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.variable.FormalParameter;
import chameleon.core.variable.VariableContainer;
import chameleon.util.Util;

/**
 * 	Represents returning advice. Returning advice exposes a single parameter, that can be used in the advice body.
 * 
 * 	@author Jens
 *
 * 	@param <E>
 */
public class Returning<E extends Returning<E>> extends ModifierImpl<E> implements Modifier<E>, VariableContainer<E> {
	/**
	 * 	Constructor
	 */
	public Returning() {

	}
	
	/**
	 * 	The return parameter
	 */
	private SingleAssociation<Returning<E>, FormalParameter> _returnParameter = new SingleAssociation<Returning<E>, FormalParameter>(this);
	
	/**
	 * 	Check if this returning modifier defines a return parameter
	 * 
	 * 	@return	True if there is a return parameter defined, false otherwise
	 */
	public boolean hasReturnParameter() {
		return (returnParameter() != null);
	}
	
	/**
	 * 	Set the return parameter
	 * 
	 * 	@param 	parameter
	 * 			The return parameter
	 */
	public void setReturnParameter(FormalParameter parameter) {
		setAsParent(_returnParameter, parameter);
	}
	
	/**
	 * 	Get the return parameter
	 * 
	 * 	@return	The return parameter
	 */
	public FormalParameter returnParameter() {
		return _returnParameter.getOtherEnd();
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language().property(ReturningProperty.ID));
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public E clone() {
		FormalParameter paramClone = null;
		
		if (returnParameter() != null)
			paramClone = returnParameter().clone();
		
		Returning<E> clone = new Returning<E>();
		clone.setReturnParameter(paramClone);
		
		return (E) clone;
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		return Util.createNonNullList(returnParameter());
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public List<? extends Declaration> locallyDeclaredDeclarations()
			throws LookupException {
		return declarations();
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public <D extends Declaration> List<D> declarations(
			DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public NamespaceElement variableScopeElement() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 	{@inheritDoc}
	 */
	@Override
	public List<? extends Element> children() {
		List<? extends Element> children = super.children();
		Util.addNonNull(returnParameter(), children);
		return children;
	}
}
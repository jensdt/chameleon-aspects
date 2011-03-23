package chameleon.aspects.advice.types;

import org.rejuse.property.PropertySet;

import chameleon.aspects.advice.properties.BeforeProperty;
import chameleon.core.element.Element;
import chameleon.core.modifier.Modifier;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;

public class Before<E extends Before<E>> extends ModifierImpl<E> implements Modifier<E> {

	@Override
	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language().property(BeforeProperty.ID));
	}

	@Override
	public E clone() {
		return (E) new Before<E>();
	}

}

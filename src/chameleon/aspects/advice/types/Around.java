package chameleon.aspects.advice.types;

import org.rejuse.property.PropertySet;

import chameleon.aspects.advice.properties.AroundProperty;
import chameleon.core.element.Element;
import chameleon.core.modifier.Modifier;
import chameleon.core.modifier.ModifierImpl;
import chameleon.core.property.ChameleonProperty;
import chameleon.support.modifier.PublicProperty;

public class Around<E extends Around<E>> extends ModifierImpl<E> implements Modifier<E> {

	@Override
	public PropertySet<Element, ChameleonProperty> impliedProperties() {
		return createSet(language().property(AroundProperty.ID));
	}

	@Override
	public E clone() {
		return (E) new Around<E>();
	}

}

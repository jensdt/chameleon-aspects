package chameleon.aspects.advice.properties;

import org.rejuse.property.PropertyMutex;
import org.rejuse.property.PropertyUniverse;

import chameleon.aspects.advice.Advice;
import chameleon.core.property.ChameleonProperty;
import chameleon.core.property.StaticChameleonProperty;

public class AfterProperty extends StaticChameleonProperty {
	
	public final static String ID = "advicetype.after";

	public AfterProperty(PropertyUniverse<ChameleonProperty> universe, PropertyMutex<ChameleonProperty> mutex) {
		super(ID, universe, mutex, Advice.class);
	}
}

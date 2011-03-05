package chameleon.aspects.namingRegistry;

import java.util.HashMap;
import java.util.Map;

import chameleon.core.element.Element;

public class NamingRegistryFactory {
	
	private NamingRegistryFactory() {
		
	}
	
	private static NamingRegistryFactory instance = new NamingRegistryFactory();
	
	public static NamingRegistryFactory instance() {
		return instance;
	}
	
	Map<String, NamingRegistry> registries = new HashMap<String, NamingRegistry>();
	
	public <T extends Element> NamingRegistry<T> getNamingRegistryFor(String name) {
		if (!registries.containsKey(name))
			registries.put(name, new NamingRegistry<T>());
		
		return registries.get(name);
	}
}
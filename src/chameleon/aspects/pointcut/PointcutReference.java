package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.SimpleNameCrossReferenceWithArgumentsSelector;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.CrossReferenceWithArguments;
import chameleon.oo.type.Type;
import chameleon.util.Util;

public class PointcutReference<E extends PointcutReference<E>> extends CrossReferenceWithArguments<E> implements CrossReference<E, Element, Pointcut>{
	
	private String _pointcutName;

	public String name() {
		return _pointcutName;
	}

	public void setName(String method) {
		_pointcutName = method;
	}
	
	@Override
	public Pointcut getElement() throws LookupException {
		return (Pointcut) super.getElement();
	}
	
	public List<Element> children() {
		List<Element> result = new ArrayList<Element>();
		result.addAll(getActualParameters());
		result.addAll(typeArguments());
		Util.addNonNull(getTarget(), result);
		return result;
	}
	
	public DeclarationSelector<Declaration> selector() throws LookupException {
		DeclarationSelector d = new SimpleNamePointcutSelector() {
			 @Override
		      public Class<Pointcut> selectedClass() {
		        return Pointcut.class;
		      }
		};
		
		return d;
	}
	
	public E clone() {
		PointcutReference<E> clone = super.clone();
		clone.setName(name());
		
		return (E) clone;
	}
	
	public abstract class SimpleNamePointcutSelector extends SimpleNameCrossReferenceWithArgumentsSelector<Pointcut> {
	  	@Override
	  	public int nbActualParameters() {
	  		return PointcutReference.this.nbActualParameters();
	  	}
	  	
	  	@Override
	  	public List<Type> getActualParameterTypes() throws LookupException {
	  		return PointcutReference.this.getActualParameterTypes();
	  	}
	  	
	  	public String name() {
	  		return PointcutReference.this.name();
	  	}
	}
}

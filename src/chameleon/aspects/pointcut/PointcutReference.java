package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersSignature;
import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.expression.InvocationTarget;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.SimpleNameCrossReferenceWithArgumentsSelector;
import chameleon.core.member.MoreSpecificTypesOrder;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.CrossReferenceWithArguments;
import chameleon.oo.type.Type;
import chameleon.oo.type.generics.ActualTypeArgument;
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
	
	@Override
	public LookupStrategy lexicalLookupStrategy(Element element)
			throws LookupException {
		if (getElement() != null)
			return getElement().header().lexicalLookupStrategy(element);
		else
			return super.lexicalLookupStrategy(element);
		
	}

	
	private LookupStrategy _local;

	private LookupStrategy _lexical;
	
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
		InvocationTarget target = null;
		if (getTarget() != null) {
			target = getTarget().clone();
		}
		final E result = (E) new PointcutReference<E>();
		result.setTarget(target);
		for (Expression element : getActualParameters()) {
			result.addArgument(element.clone());
		}
		for (ActualTypeArgument arg : typeArguments()) {
			result.addArgument(arg.clone());
		}
		
		result.setName(name());
		
		return result;
	}
	
	public abstract class SimpleNamePointcutSelector<D extends Pointcut> extends SimpleNameCrossReferenceWithArgumentsSelector<D> {
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
	  	
		@Override
		public boolean selectedRegardlessOfName(D declaration)
				throws LookupException {
			return true;
		}
	}
}

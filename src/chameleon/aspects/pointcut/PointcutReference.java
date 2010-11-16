package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.TwoPhaseDeclarationSelector;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.CrossReferenceWithArguments;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.oo.type.Type;
import chameleon.support.member.MoreSpecificTypesOrder;
import chameleon.support.member.simplename.method.NormalMethod;
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
	
	  public abstract class SimpleNamePointcutSelector extends TwoPhaseDeclarationSelector<Pointcut> {
		  	
//	  	private int _nameHash = SimpleNameMethodInvocation.this._methodName.hashCode();
	    
	  	@Override
	    public boolean selectedRegardlessOfName(Pointcut declaration) throws LookupException {
	  		boolean result = false;
	  		
			Signature signature = declaration.signature();
			if (signature instanceof PointcutSignature) {
				PointcutSignature sig = (PointcutSignature) signature;
				if (sig.nbFormalParameters() == nbActualParameters()) {
					result = true;
//					List<Type> actuals = getActualParameterTypes();
//					List<Type> formals = sig.parameterTypes();
//					result = MoreSpecificTypesOrder.create().contains(actuals,
//							formals);
				} else {
					result = false;
				}
			}
			return result;
		}
	  	
	  	@Override
	  	public String selectionName(DeclarationContainer container) {
	  		return name();
	  	}
	    
	  	@Override
	    public boolean selectedBasedOnName(Signature signature) throws LookupException {
	  		boolean result = false;
	  		if(signature instanceof PointcutSignature) {
	  			PointcutSignature sig = (PointcutSignature)signature;
	  			result = sig.name().equals(name()); // (_nameHash == sig.nameHash()) && 
	  		}
	  		return result;
	    }

	    @Override
	    public WeakPartialOrder<Pointcut> order() {
	      return new WeakPartialOrder<Pointcut>() {
	        @Override
	        public boolean contains(Pointcut first, Pointcut second)
	            throws LookupException {
	          return MoreSpecificTypesOrder.create().contains(((PointcutHeader) first.header()).formalParameterTypes(), ((PointcutHeader) second.header()).formalParameterTypes());
	        }
	      };
	    }
	  }
}

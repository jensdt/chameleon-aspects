package chameleon.aspects.pointcut;

import java.lang.ref.SoftReference;
import java.util.List;

import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.java.collections.Visitor;

import chameleon.aspects.pointcut.expression.PointcutExpressionParameter;
import chameleon.core.Config;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.DeclarationWithParametersHeader;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.DeclaratorSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.TwoPhaseDeclarationSelector;
import chameleon.core.member.MoreSpecificTypesOrder;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.CrossReferenceWithArguments;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;

public class PointcutReference<E extends PointcutReference<E>> extends
		NamespaceElementImpl<E> implements CrossReference<E, Pointcut> {

	private String _pointcutName;

	public String name() {
		return _pointcutName;
	}

	public void setName(String method) {
		_pointcutName = method;
	}

	public PointcutReference(String name) {
		setName(name);
	}

	private OrderedMultiAssociation<PointcutReference<E>, PointcutExpressionParameter> _params = new OrderedMultiAssociation<PointcutReference<E>, PointcutExpressionParameter>(
			this);

	public List<PointcutExpressionParameter> parameters() {
		return _params.getOtherEnds();
	}

	public void addParameter(PointcutExpressionParameter param) {
		setAsParent(_params, param);
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

	public class SimpleNamePointcutSelector<D extends Pointcut> extends
			TwoPhaseDeclarationSelector<D> {

		@Override
		public boolean selectedRegardlessOfName(D declaration)
				throws LookupException {
			System.out.println(declaration.header().formalParameters().size());
			return true;
		}

		@Override
		public boolean selectedBasedOnName(Signature signature)
				throws LookupException {
			boolean result = false;
			if (signature instanceof SimpleNameDeclarationWithParametersSignature) {
				SimpleNameDeclarationWithParametersSignature sig = (SimpleNameDeclarationWithParametersSignature) signature;
				result = sig.name().equals(name()); // (_nameHash ==
													// sig.nameHash())
													// &&
			}
			return result;
		}

		@Override
		public String selectionName(DeclarationContainer<?> container)
				throws LookupException {
			return PointcutReference.this.name();
		}

		@Override
		public Class<D> selectedClass() {
			return (Class<D>) Pointcut.class;
		}

		@Override
		public WeakPartialOrder<D> order() {
			return new WeakPartialOrder<D>() {
				@Override
				public boolean contains(D first, D second)
						throws LookupException {
					return MoreSpecificTypesOrder
							.create()
							.contains(
									((DeclarationWithParametersHeader) first
											.header()).formalParameterTypes(),
									((DeclarationWithParametersHeader) second
											.header()).formalParameterTypes());
				}
			};
		}
	}

	@Override
	public List<? extends Element> children() {
		return parameters();
	}

	@Override
	public Pointcut getElement() throws LookupException {
		Pointcut el = (Pointcut) getElement(selector());
		if (el == null) // debug
			getElement(selector());

		return el;
	}

	@Override
	public Declaration getDeclarator() throws LookupException {
		return getElement(new DeclaratorSelector(selector()));
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	public <X extends Declaration> X getElement(DeclarationSelector<X> selector)
			throws LookupException {
		X result = null;

		// OPTIMISATION
		boolean cache = selector.equals(selector());
		if (cache) {
			result = (X) getCache();
		}
		if (result != null) {
			return result;
		}

		result = lexicalLookupStrategy().lookUp(selector);

		if (result != null) {
			// OPTIMISATION
			if (cache) {
				setCache((Declaration) result);
			}
			return result;
		} else {
			// repeat lookup for debugging purposes.
			// Config.setCaching(false);
			result = lexicalLookupStrategy().lookUp(selector);
			throw new LookupException("Method returned by invocation is null");
		}
	}

	private SoftReference<Declaration> _cache;

	@Override
	public void flushLocalCache() {
		super.flushLocalCache();
		_cache = null;
	}

	public Declaration getCache() {
		Declaration result = null;
		if (Config.cacheElementReferences() == true) {
			result = (_cache == null ? null : _cache.get());
		}
		return result;
	}

	public void setCache(Declaration value) {
		// if(! value.isDerived()) {
		if (Config.cacheElementReferences() == true) {
			_cache = new SoftReference<Declaration>(value);
		}
		// } else {
		// _cache = null;
		// }
	}

	@Override
	public E clone() {
		final PointcutReference result = new PointcutReference(name());
		new Visitor<PointcutExpressionParameter>() {
			public void visit(PointcutExpressionParameter element) {
				result.addParameter(element.clone());
			}
		}.applyTo(parameters());
		
		return (E) result;
	}

	public void addAllArguments(List<PointcutExpressionParameter> arguments) {
		for (PointcutExpressionParameter param : arguments)
			addParameter(param);
	}

	public boolean hasParameter(FormalParameter fp) {
		return indexOfParameter(fp) != -1;
	}
	
	public int indexOfParameter(FormalParameter fp) {
		int index = 0;
		for (PointcutExpressionParameter param : parameters()) {
			if (param.name().equals(fp.getName()))
				return index;
			
			index++;
		}
		
		return -1;
	}
	
}

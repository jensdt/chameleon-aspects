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
import chameleon.core.lookup.SimpleNameCrossReferenceWithArgumentsSelector;
import chameleon.core.lookup.TwoPhaseDeclarationSelector;
import chameleon.core.member.MoreSpecificTypesOrder;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.reference.CrossReference;
import chameleon.core.reference.CrossReferenceWithArguments;
import chameleon.core.relation.WeakPartialOrder;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.Type;
import chameleon.support.member.simplename.SimpleNameMethodInvocation;

public class PointcutReference<E extends PointcutReference<E>> extends
		CrossReferenceWithArguments<E> implements CrossReference<E, Pointcut> {

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

	@Override
	public Pointcut getElement() throws LookupException {
		return (Pointcut) super.getElement();
	}
	
	@Override
	public DeclarationSelector<Declaration> selector() throws LookupException {
		return new SimpleNameMethodSelector();
	}

	public class SimpleNameMethodSelector<D extends Pointcut> extends SimpleNameCrossReferenceWithArgumentsSelector<D> {

		@Override
		public String name() {
			return PointcutReference.this.name();
		}

		@Override
		public int nbActualParameters() {
			return PointcutReference.this.nbActualParameters();
		}

		@Override
		public List<Type> getActualParameterTypes() throws LookupException {
			return PointcutReference.this.getActualParameterTypes();
		}

		@Override
		public Class<D> selectedClass() {
			return (Class<D>) Pointcut.class;
		}
		
		
	}


	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}


	
}

package chameleon.aspects;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.MultiAssociation;
import org.rejuse.association.OrderedMultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.pointcut.Pointcut;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.lookup.LookupStrategyFactory;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.scope.Scope;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ChameleonProgrammerException;
import chameleon.exception.ModelException;
import chameleon.util.Util;

public class Aspect<E extends Aspect<E>> extends NamespaceElementImpl<E> implements DeclarationContainer<E>, Declaration<E,  SimpleNameSignature>{
	
	public Aspect(String name) {
		this(new SimpleNameSignature(name));
	}
	
	public Aspect(SimpleNameSignature sig) {
		setSignature(sig);
	}
	
	public String name() {
		return signature().name();
	}
	
	/**
	 * 	Get the list of pointcuts that have been defined in this Aspect
	 */
	public List<Pointcut> pointcuts() {
		return _pointcuts.getOtherEnds();
	}
	
	private OrderedMultiAssociation<Aspect, Pointcut> _pointcuts = new OrderedMultiAssociation<Aspect, Pointcut>(this);
	
	public OrderedMultiAssociation<Aspect, Pointcut> pointcutLink() {
		return _pointcuts;
	}
	
	public void addPointcut(Pointcut e) {
		setAsParent(_pointcuts, e);
	}

	
	/**
	 * 	Get the list of advices that have been defined in this Aspect
	 */
	public List<Advice> advices() {
		return _advices.getOtherEnds();
	}
	
	private MultiAssociation<Aspect, Advice> _advices = new MultiAssociation<Aspect, Advice>(this);
	
	public MultiAssociation<Aspect, Advice> adviceLink() {
		return _advices;
	}
	
	public void addAdvice(Advice e) {
		setAsParent(_advices, e);
	}

	@Override
	public List<Element> children() {
		List<Element> children = new ArrayList<Element>();
		children.addAll(pointcuts());
		children.addAll(advices());
		Util.addNonNull(signature(), children);
		return children;
	}

	public E clone() {
		Aspect<E> clone = new Aspect<E>(signature() == null ? null : signature().clone());
		
		for (Pointcut pc : pointcuts()) {
			Pointcut pcClone = pc.clone();
			clone.addPointcut(pcClone);
		}
		
		for (Advice ac : advices()) {
			Advice adviceClone = ac.clone();
			clone.addAdvice(adviceClone);
		}
		
		return (E) clone;
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		return pointcuts();
	}

	@Override
	public List<? extends Declaration> locallyDeclaredDeclarations()
			throws LookupException {
		return declarations();
	}

	@Override
	public <D extends Declaration> List<D> declarations(
			DeclarationSelector<D> selector) throws LookupException {
		return selector.selection(declarations());
	}

	@Override
	public LookupStrategy lexicalLookupStrategy(Element child)
			throws LookupException {
		LookupStrategyFactory lookupFactory = language().lookupFactory();
		return lookupFactory.createLexicalLookupStrategy(lookupFactory.createLocalLookupStrategy(this), this);
	}
	
	private SingleAssociation<Aspect, SimpleNameSignature> _signature = new SingleAssociation<Aspect, SimpleNameSignature>(this);

	@Override
	public SimpleNameSignature signature() {
		return _signature.getOtherEnd();
	}

	@Override
	public void setSignature(Signature signature) {
		if (!(signature instanceof SimpleNameSignature))
			throw new ChameleonProgrammerException("Exptected simpleNameSignature, got " + signature);
		
		setAsParent(_signature, (SimpleNameSignature) signature);
	}


	@Override
	public Declaration<?, ?> selectionDeclaration()
			throws LookupException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Declaration actualDeclaration() throws LookupException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Declaration declarator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scope scope() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		setSignature(new SimpleNameSignature(name));
	}
}
package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.Aspect;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.DeclarationWithHeader;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.scope.Scope;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.exception.ModelException;
import chameleon.util.Util;

/**
 *
 *	A Pointcut picks out joinpoints in the program flow.
 *
 *	TODO: more doc
 * 	
 * 	@author Jens De Temmerman
 *
 */
public abstract class Pointcut<E extends Pointcut<E>> extends NamespaceElementImpl<E, Element> implements DeclarationContainer<E,Element>, DeclarationWithHeader<E, Element, SimpleNameDeclarationWithParametersSignature, Declaration, SimpleNameDeclarationWithParametersHeader> {
	
	public Pointcut() {
		
	}
	
	public Pointcut(SimpleNameDeclarationWithParametersHeader header) {
		setHeader(header);
	}

	public Pointcut(SimpleNameDeclarationWithParametersHeader header, PointcutExpression expression) {
		this(header);
		setExpression(expression);
	}

	/**
	 * 	Get the Aspect that this Pointcut belongs to
	 */
	public Aspect aspect() {
		return (Aspect) parent();
		
	}
	
	private SingleAssociation<Pointcut<E>, SimpleNameDeclarationWithParametersHeader> _header = new SingleAssociation<Pointcut<E>, SimpleNameDeclarationWithParametersHeader>(this);
	
	public SimpleNameDeclarationWithParametersHeader header() {
		return _header.getOtherEnd();
	}
	
	protected void setHeader(SimpleNameDeclarationWithParametersHeader header) {
		setAsParent(_header, header);
	}

//	@Override
//	public void setSignature(Signature signature) {
//	  	if(signature instanceof PointcutSignature) {
//	  		_signature.connectTo(signature.parentLink());
//	  	} else if(signature == null) {
//	  		_signature.connectTo(null);
//	  	} else {
//	  		throw new ChameleonProgrammerException("Setting wrong type of signature. Provided: "+(signature == null ? null :signature.getClass().getName())+" Expected PointcutSignature");
//	  	}
//	}
	
	private SingleAssociation<Pointcut, PointcutExpression> _expression = new SingleAssociation<Pointcut, PointcutExpression>(this);
	
	public PointcutExpression expression() {
		return _expression.getOtherEnd();
	}
	
	protected void setExpression(PointcutExpression expression) {
		setAsParent(_expression, expression);
	}
	
	public abstract List<? extends Element> joinpoints() throws LookupException;
		
	public abstract E clone();
	
	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		
		if (aspect() == null)
			result.and(new BasicProblem(this, "Pointcuts must be defined within aspects."));
		
		return result;
	}
	
	@Override
	public List<? extends Element> children() {
		List<Element> children = new ArrayList<Element>();
		
		Util.addNonNull(header(), children);
		Util.addNonNull(expression(), children);
		
		return children;
	}
	
	@Override
	public Declaration<?, ?, ?, Declaration> selectionDeclaration()
			throws LookupException {
		return this;
	}
	
	@Override
	public Declaration actualDeclaration() throws LookupException {
		return this;
	}
	

	@Override
	public Declaration declarator() {
		return this;
	}

	@Override
	public SimpleNameDeclarationWithParametersSignature signature() {
		return header().signature();
	}

	@Override
	public void setSignature(Signature signature) {
		header().createFromSignature(signature);
	}

	@Override
	public void setName(String name) {
		header().setName(name);
	}

	@Override
	public Scope scope() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Declaration> declarations() throws LookupException {
		return header().declarations();
	}

	@Override
	public List<? extends Declaration> locallyDeclaredDeclarations()
			throws LookupException {
		return declarations();
	}

	@Override
	public <D extends Declaration> List<D> declarations(
			DeclarationSelector<D> selector) throws LookupException {
		return header().declarations(selector);
	}
}

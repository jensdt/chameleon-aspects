package chameleon.aspects;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.MultiAssociation;
import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcutexpression.PointcutExpression;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.LookupException;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
import chameleon.exception.ChameleonProgrammerException;
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
public abstract class Pointcut<E extends Pointcut<E>> extends NamespaceElementImpl<E, Element> implements Declaration<E, Element, SimpleNameSignature, Declaration> {
	
	public Pointcut() {
		
	}
	
	public Pointcut(SimpleNameSignature signature) {
		setSignature(signature);
	}
	
	public Pointcut(SimpleNameSignature signature, PointcutExpression expression) {
		this(signature);
		setExpression(expression);
	}
	
	public Pointcut(SimpleNameSignature signature, PointcutExpression expression, List<FormalParameter> formalParameters) {
		this(signature, expression);
		addFormalParameter(formalParameters);
	}
	


	/**
	 * 	Get the Aspect that this Pointcut belongs to
	 */
	public Aspect aspect() {
		return (Aspect) parentLink().getOtherEnd();
		
	}
	
	 private MultiAssociation<Pointcut<E>, FormalParameter> _formalParameters = new MultiAssociation<Pointcut<E>, FormalParameter>(this);
	 
	private void addFormalParameter(List<FormalParameter> formalParameters) {
		if (formalParameters == null)
			return;
		
		for (FormalParameter par : formalParameters)
			addFormalParameter(par);
	}

	private void addFormalParameter(FormalParameter par) {
		setAsParent(_formalParameters, par);
	}
	
	public List<FormalParameter> formalParameters() {
		return _formalParameters.getOtherEnds();
	}
	
	 private SingleAssociation<Pointcut<E>, SimpleNameSignature> _signature = new SingleAssociation<Pointcut<E>, SimpleNameSignature>(this);
	
	@Override
	public SimpleNameSignature signature() {
		return _signature.getOtherEnd();
	}

	@Override
	public void setSignature(Signature signature) {
	  	if(signature instanceof SimpleNameSignature) {
	  		_signature.connectTo(signature.parentLink());
	  	} else if(signature == null) {
	  		_signature.connectTo(null);
	  	} else {
	  		throw new ChameleonProgrammerException("Setting wrong type of signature. Provided: "+(signature == null ? null :signature.getClass().getName())+" Expected SimpleNameSignature");
	  	}
	}
	
	private SingleAssociation<Pointcut, PointcutExpression> _expression = new SingleAssociation<Pointcut, PointcutExpression>(this);
	
	public PointcutExpression expression() {
		return _expression.getOtherEnd();
	}
	
	protected void setExpression(PointcutExpression expression) {
		setAsParent(_expression, expression);
	}
	
	public abstract List<? extends Element> joinpoints() throws LookupException;
	
	public void setName(String name) {
		setSignature(new SimpleNameSignature(name));
	}
	
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
		
		Util.addNonNull(expression(), children);
		children.addAll(formalParameters());
		
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
}

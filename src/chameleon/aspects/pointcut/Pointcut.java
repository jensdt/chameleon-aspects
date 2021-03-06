package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.Aspect;
import chameleon.aspects.pointcut.expression.PointcutExpression;
import chameleon.core.declaration.Declaration;
import chameleon.core.declaration.DeclarationContainer;
import chameleon.core.declaration.Signature;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersSignature;
import chameleon.core.element.Element;
import chameleon.core.lookup.DeclarationSelector;
import chameleon.core.lookup.LookupException;
import chameleon.core.lookup.LookupStrategy;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.scope.Scope;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.core.variable.FormalParameter;
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
public class Pointcut<E extends Pointcut<E>> extends NamespaceElementImpl<E> implements DeclarationContainer<E>, Declaration<E, SimpleNameDeclarationWithParametersSignature> {
	
	public Pointcut() {
		
	}
	
	public Pointcut(SimpleNameDeclarationWithParametersHeader header) {
		setHeader(header);
	}

	public Pointcut(SimpleNameDeclarationWithParametersHeader header, PointcutExpression expression) {
		this(header);
		setExpression(expression);
	}
	
	public LookupStrategy lexicalLookupStrategy(Element element) throws LookupException {
		if (element == header()) {
			return parent().lexicalLookupStrategy(this);
		} else {
			if (_lexical == null) {
				_lexical = language().lookupFactory()
						.createLexicalLookupStrategy(localLookupStrategy(),
								this);
			}
			return _lexical;
		}
	}

	public LookupStrategy localLookupStrategy() {
		if (_local == null) {
			_local = language().lookupFactory()
					.createTargetLookupStrategy(this);
		}
		return _local;
	}

	private LookupStrategy _local;

	private LookupStrategy _lexical;

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
	
	private SingleAssociation<Pointcut, PointcutExpression> _expression = new SingleAssociation<Pointcut, PointcutExpression>(this);
	
	public PointcutExpression expression() {
		return _expression.getOtherEnd();
	}
	
	protected void setExpression(PointcutExpression expression) {
		setAsParent(_expression, expression);
	}
	
	public E clone() {
		Pointcut<E> clone = new Pointcut<E>();
		clone.setHeader((SimpleNameDeclarationWithParametersHeader) header().clone());
		clone.setExpression((PointcutExpression) expression().clone());
		
		return (E) clone;
	}
	
	public List<FormalParameter> parameters() {
		return header().formalParameters();
	}
	
	private List<FormalParameter> unresolvedParameters() {
		List<FormalParameter> unresolved = new ArrayList<FormalParameter>();
		
		for (FormalParameter fp : (List<FormalParameter>) header().formalParameters())
			if (!expression().hasParameter(fp))
				unresolved.add(fp);
		
		return unresolved;
	}
	
	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		
		if (aspect() == null)
			result = result.and(new BasicProblem(this, "Pointcuts must be defined within aspects."));
		
		List<FormalParameter> unresolved = unresolvedParameters();
		if (!unresolved.isEmpty()) {
			
			StringBuffer unresolvedList = new StringBuffer();
			Iterator<FormalParameter> it = unresolved.iterator();
			unresolvedList.append(it.next().getName());
			
			while (it.hasNext()) {
				unresolvedList.append(", ");
				unresolvedList.append(it.next().getName());
			}
			
			result = result.and(new BasicProblem(this, "The following parameters cannot be resolved: " + unresolvedList));
		}
		
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
	public Declaration<?, ?> selectionDeclaration()
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
		setHeader(header().createFromSignature(signature));
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

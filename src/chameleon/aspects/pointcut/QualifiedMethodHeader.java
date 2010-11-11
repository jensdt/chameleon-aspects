package chameleon.aspects.pointcut;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.QualifiedName;
import chameleon.core.declaration.Signature;
import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.util.Util;

public class QualifiedMethodHeader<E extends QualifiedMethodHeader<E>> extends NamespaceElementImpl<E, Element> {
	
	private SingleAssociation<QualifiedMethodHeader, QualifiedName> _prefixes = new SingleAssociation<QualifiedMethodHeader, QualifiedName>(this);
	private SingleAssociation<QualifiedMethodHeader, PointcutMethodHeader> _methodHeader = new SingleAssociation<QualifiedMethodHeader, PointcutMethodHeader>(this);
	
	public QualifiedMethodHeader() {
		super();
	}
	
	public QualifiedMethodHeader(PointcutMethodHeader header) {
		setMethodheader(header);
	}
	
	public PointcutMethodHeader methodHeader() {
		return _methodHeader.getOtherEnd();
	}
	
	public void setMethodheader(PointcutMethodHeader header) {
		setAsParent(_methodHeader, header);
	}
	
	public Signature signature() {
		return null; // TODO
	}
	
	public QualifiedName prefixes() {
		return _prefixes.getOtherEnd();
	}
	
	public void setPrefixes(QualifiedName prefixes) {
		setAsParent(_prefixes, prefixes);
	}
	
	public List<Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(prefixes(), result);
		Util.addNonNull(methodHeader(), result);
		
		return result;
	}

	@Override
	public VerificationResult verifySelf() {
		VerificationResult result = Valid.create();
		
		return result;
	}

	@Override
	public E clone() {
		// TODO Auto-generated method stub
		return null;
	}


}

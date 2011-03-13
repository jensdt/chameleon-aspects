package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.ArrayList;
import java.util.List;

import org.rejuse.association.SingleAssociation;

import chameleon.core.declaration.QualifiedName;
import chameleon.core.declaration.SimpleNameDeclarationWithParametersHeader;
import chameleon.core.declaration.SimpleNameSignature;
import chameleon.core.element.Element;
import chameleon.core.namespace.NamespaceElementImpl;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.util.Util;

public class QualifiedMethodHeader<E extends QualifiedMethodHeader<E>> extends NamespaceElementImpl<E> {
	
	private SingleAssociation<QualifiedMethodHeader, QualifiedName> _prefixes = new SingleAssociation<QualifiedMethodHeader, QualifiedName>(this);
	private SingleAssociation<QualifiedMethodHeader, SimpleNameDeclarationWithParameterTypesHeader> _methodHeader = new SingleAssociation<QualifiedMethodHeader, SimpleNameDeclarationWithParameterTypesHeader>(this);
	
	public QualifiedMethodHeader() {
		super();
	}
	
	public QualifiedMethodHeader(SimpleNameDeclarationWithParameterTypesHeader header) {
		setMethodheader(header);
	}
	
	public SimpleNameDeclarationWithParameterTypesHeader methodHeader() {
		return _methodHeader.getOtherEnd();
	}
	
	public void setMethodheader(SimpleNameDeclarationWithParameterTypesHeader header) {
		setAsParent(_methodHeader, header);
	}
	
	public String getFullyQualifiedName() {
		StringBuilder fqn = new StringBuilder();
		
		for (Object o : prefixes().signatures()) {
			SimpleNameSignature s = (SimpleNameSignature) o;
			fqn.append(s.name());
			fqn.append(".");
		}
		
		if (fqn.length() > 0) // Trim the trailing dot
			fqn.setLength(fqn.length()-1);
		
		return fqn.toString();
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
		QualifiedMethodHeader result = new QualifiedMethodHeader();
		result.setPrefixes(prefixes().clone());
		result.setMethodheader((SimpleNameDeclarationWithParameterTypesHeader) methodHeader().clone());
		
		return (E) result;
	}


}

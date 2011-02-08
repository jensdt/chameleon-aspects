package chameleon.aspects.pointcut.expression;

import java.util.Collections;
import java.util.List;

import chameleon.core.element.Element;
import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.Type;

public class PointcutExpressionParameter<E extends PointcutExpressionParameter<E>> extends Expression<E> {

	private String name;
	
	public PointcutExpressionParameter(String name) {
		setName(name);
	}
	
	public String name() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	@Override
	public List<? extends Element> children() {
		return Collections.emptyList();
	}

	@Override
	public E clone() {
		return (E) new PointcutExpressionParameter(name);
	}

	@Override
	public VerificationResult verifySelf() {
		return Valid.create();
	}

	@Override
	protected Type actualType() throws LookupException {
		// TODO Auto-generated method stub
		return null;
	}

}

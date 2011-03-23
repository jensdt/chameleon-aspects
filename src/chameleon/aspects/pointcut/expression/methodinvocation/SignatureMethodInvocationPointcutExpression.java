package chameleon.aspects.pointcut.expression.methodinvocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.RuntimeErrorException;

import jnome.core.type.RegularJavaType;

import org.rejuse.association.SingleAssociation;

import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.element.Element;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.lookup.LocalLookupStrategy;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.variable.FormalParameter;
import chameleon.oo.type.Type;
import chameleon.oo.type.TypeReference;
import chameleon.util.Util;

public class SignatureMethodInvocationPointcutExpression<E extends SignatureMethodInvocationPointcutExpression<E>> extends MethodInvocationPointcutExpression<E> {
	private SingleAssociation<SignatureMethodInvocationPointcutExpression, MethodReference> _methodReference = new SingleAssociation<SignatureMethodInvocationPointcutExpression, MethodReference>(this);
	
	public SignatureMethodInvocationPointcutExpression(MethodReference methodReference) {
		setMethodReference(methodReference);
	}

	private void setMethodReference(MethodReference methodReference) {
		setAsParent(_methodReference, methodReference);
	}
	
	private MethodReference methodReference() {
		return _methodReference.getOtherEnd();
	}

	@Override
	public MatchResult matches(Element element) throws LookupException {
		if (!(element instanceof MethodInvocation))
			return MatchResult.noMatch();
		
		MethodInvocation joinpoint = (MethodInvocation) element;
		
		Method e = joinpoint.getElement();

		// Check if the returntype matches
		// Types match if: the type of the method is a (sub)type of the type declared in the pointcut expression.
		// E.g. public Integer getCalculationResult() matches call(Number getCalculationResult)
		
		// If the type name contains no wild cards, it is as a type that is known (e.g. through a fqn or imports).
		// If it does contain wild cards, do string comparison to check
		boolean matches = false;
		if (methodReference().hasExplicitType())
			 matches = e.returnType().assignableTo(methodReference().type().getType());
		else {
			matches = sameAsWithWildcard(e.returnType().getFullyQualifiedName(), methodReference().typeNameWithWC());
			
			Iterator<Type> superTypeIterator = e.returnType().getAllSuperTypes().iterator();
			
			while (!matches && superTypeIterator.hasNext()) {
				matches = sameAsWithWildcard(superTypeIterator.next().getFullyQualifiedName(), methodReference().typeNameWithWC());
			}
		}
		
		if (!matches)
			return MatchResult.noMatch();
				
		
		// Check if the signature matches
		if (!sameAsWithWildcard(e.signature().name(), methodReference().fqn().methodHeader().name()))
			return MatchResult.noMatch();
		
		// Check if the FQN matches
		Type definedType = ((RegularJavaType) ((LocalLookupStrategy) joinpoint.getTarget().targetContext()).declarationContainer()).getType();
		String definedFqn = methodReference().getFullyQualifiedName();

		matches = sameFQNWithWildcard(definedType.getFullyQualifiedName(), definedFqn);
		
		Iterator<Type> superTypeIterator = definedType.getAllSuperTypes().iterator();
		while (!matches && superTypeIterator.hasNext()) {
			
			matches = sameFQNWithWildcard(superTypeIterator.next().getFullyQualifiedName(), definedFqn);
		}
		
		if (!matches)
			return MatchResult.noMatch();
		
		// Check if the parameter types match
		Iterator<FormalParameter> methodArguments = e.formalParameters().iterator();
		Iterator<TypeReference> argumentTypes = methodReference().fqn().methodHeader().types().iterator();
	
		
		while (methodArguments.hasNext() && argumentTypes.hasNext()) {
			TypeReference argType = argumentTypes.next();
			FormalParameter methodArg = methodArguments.next();
			
			if (!methodArg.getType().assignableTo(argType.getType()))	
				return MatchResult.noMatch();
		}
		
		// If this is true, it means there is a difference in the number of args
		if (methodArguments.hasNext() || argumentTypes.hasNext())
			return MatchResult.noMatch();
		
		return new MatchResult<SignatureMethodInvocationPointcutExpression, MethodInvocation>(this, (MethodInvocation) joinpoint);
	}
	
	private boolean containsWildcards(String type) {
		return type.contains("**");
	}

	/**
	 * 	Match rules:
	 * 			hrm.Person matches with:
	 * 				- hrm.Person (or any wildcard combo, e.g. h*m.P*)
	 * 				- **.Person  (or any wildcard combo, e.g. **.P*)
	 * 				- hrm.**     (or any wildcard combo, e.g. h*m.**)
	 * 				- **.**
	 * 				- **
	 * 
	 * @param jpFqn_
	 * @param definedFqn_
	 * @return
	 */
	private boolean sameFQNWithWildcard(String jpFqn_, String definedFqn_) {
		String[] jpFqn = jpFqn_.split("\\.");
		String[] definedFqn = definedFqn_.split("\\.");
		
		// Special case: if the FQN of the call is a complete wildcard, match everything
		if (definedFqn.length == 1 && definedFqn[0].equals("**"))
			return true;
		
		if (jpFqn.length != definedFqn.length)
			return false;
		
		for (int i = 0; i < jpFqn.length; i++)
			if (!sameAsWithWildcard(jpFqn[i], definedFqn[i]))
				return false;
		
		return true;
	}

	/**
	 * 	Check if s1 is the same as s2 - s2 can contain a wild card (** = any character 0 or more times), s1 can
	 *  contain the wild card character but it will not be treated as such.
	 *  
	 */
	public boolean sameAsWithWildcard(String s1, String s2) {
		// Turn s2 into a regexp. We convert the wild card character (**) to a regexp-wildcard (.*) and treat
		// all the rest as a literal (between \Q and \E)
		String regexp = "\\Q" + s2.replace("**", "\\E(.*)\\Q") + "\\E"; 
		
		return s1.matches(regexp);
	}

	@Override
	public List<? extends Element> children() {
		List<Element> result = new ArrayList<Element>();
		
		Util.addNonNull(methodReference(), result);
		
		return result;
	}

	@Override
	public E clone() {
		return (E) new SignatureMethodInvocationPointcutExpression<E>(methodReference().clone()); 
	}

	@Override
	public MatchResult matchesInverse(Element joinpoint) throws LookupException {
		return super.matchesInverse(joinpoint);
	}	
}

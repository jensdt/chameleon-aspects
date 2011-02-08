package chameleon.aspects.advice;

import chameleon.core.expression.Expression;
import chameleon.core.lookup.LookupException;
import chameleon.core.method.Method;
import chameleon.core.validation.BasicProblem;
import chameleon.core.validation.Valid;
import chameleon.core.validation.VerificationResult;
import chameleon.oo.type.Type;
import chameleon.support.statement.ReturnStatement;

public class AdviceReturnStatement extends ReturnStatement {
	  @Override
	  public VerificationResult verifySelf() {
	  	VerificationResult result = Valid.create();
	  	
	  	try {
	  			Advice nearestAncestor = nearestAncestor(Advice.class);
				if(nearestAncestor != null) {
					Expression expression = getExpression();
					if(expression != null) {
				    Type returnType = nearestAncestor.returnType().getType();
				    // problem with the type of the return value will be reported by the expression.
				    Type type = expression.getType();
				    try {
				    	if(! type.subTypeOf(returnType)) {
				    		result = result.and(new BasicProblem(this, "The type of the return value is not a subtype of the return type of the advice."));
				    	}
				    }catch (LookupException e) {
							result = result.and(new BasicProblem(this, "Cannot determine the relation between the type of the return value and the return type of the advice."));
						}
					}
				} else {
					result = result.and(new BasicProblem(this, "The return statement is not contained in an advice."));
				}
			} catch (LookupException e) {
				// If there is a return type, but it cannot be resolved, that crossreference will report the problem.
			}
	  	return result;
	  }
}

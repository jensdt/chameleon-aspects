package chameleon.aspects.advice.types.methodInvocation;

import java.util.List;
import java.util.Map;

import jnome.core.expression.ArrayCreationExpression;
import jnome.core.expression.ArrayInitializer;
import jnome.core.type.ArrayTypeReference;
import jnome.core.type.BasicJavaTypeReference;
import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.AdviceReturnStatement;
import chameleon.aspects.advice.types.Around;
import chameleon.core.expression.Expression;
import chameleon.core.expression.MethodInvocation;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.oo.type.BasicTypeReference;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.NullLiteral;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;

public class AroundReflectiveMethodInvocation extends ReflectiveMethodInvocation implements Around<Block, MethodInvocation> {

	public AroundReflectiveMethodInvocation(Advice advice, Map<String, String> variableNames) {
		super("around", advice, variableNames);
	}

	@Override
	protected Block getInnerBody() throws LookupException {
		Block adviceBody = (Block) advice().body().clone();
		
		String objectParamName = getVariableNames().get("objectParamName");
		String methodNameParamName = getVariableNames().get("methodNameParamName");
		String argumentNameParamName = getVariableNames().get("argumentNameParamName");
		
		// Replace each proceed call to the method call
		List<ProceedCall> proceedCalls = adviceBody.descendants(ProceedCall.class);
		
		for (ProceedCall pc : proceedCalls) {
			// Create the correct parameters for the proceed call
			ArrayCreationExpression actualArgumentsArray = new ArrayCreationExpression(new ArrayTypeReference(new BasicJavaTypeReference("Object")));
			ArrayInitializer actualArgumentsInitializer = new ArrayInitializer();					
		
			for (Expression e : pc.getActualParameters())
				actualArgumentsInitializer.addInitializer(e);
			
			actualArgumentsArray.setInitializer(actualArgumentsInitializer);
			
			RegularMethodInvocation proceedInvoc = createProceedInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(methodNameParamName), actualArgumentsArray);
			
			pc.parentLink().getOtherRelation().replace(pc.parentLink(), proceedInvoc.parentLink());
		}
		
		if (advice().returnType().getType().signature().name().equals("void"))
			// We need an explicit return because the return type of the advice method is never 'void'
			adviceBody.addStatement(new ReturnStatement(new NullLiteral()));
		else {
			// We need to find all return statements and cast the return expression to the return type.
			// For instance in Java, the return type will be generic (T), so we need to cast to T. This isn't *always* necessary (e.g. return proceed())
			// but no harm is done if we do it anyway in those cases.
			for (AdviceReturnStatement st : adviceBody.descendants(AdviceReturnStatement.class)) {
				ClassCastExpression cast = new ClassCastExpression(new BasicTypeReference("T"), st.getExpression());
				st.setExpression(cast);
			}
		}
		
		return adviceBody;
	}

	protected boolean encloseWithTry() {
		return !advice().body().descendants(ProceedCall.class).isEmpty();
	}
}

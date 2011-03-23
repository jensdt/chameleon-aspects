package chameleon.aspects.advice.types.translation.reflection.fieldaccess;

import java.util.List;

import jnome.core.language.Java;
import chameleon.aspects.advice.AdviceReturnStatement;
import chameleon.aspects.advice.types.ProceedCall;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.expression.Expression;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.core.lookup.LookupException;
import chameleon.core.statement.Block;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.Type;
import chameleon.support.expression.ClassCastExpression;
import chameleon.support.expression.NullLiteral;
import chameleon.support.member.simplename.method.RegularMethodInvocation;
import chameleon.support.statement.ReturnStatement;

public class AroundReflectiveFieldRead extends ReflectiveFieldRead {

	public AroundReflectiveFieldRead(MatchResult<?, ?> joinpoint) {
		super(joinpoint);
	}

	@Override
	protected Block getBody() {
		Block adviceBody = (Block) advice().body().clone();
		
		// Replace each proceed call to the method call
		List<ProceedCall> proceedCalls = adviceBody.descendants(ProceedCall.class);
		
		for (ProceedCall pc : proceedCalls) {
			RegularMethodInvocation<?> getValueInvocation = createGetFieldValueInvocation(new NamedTarget(advice().aspect().name()), new NamedTargetExpression(objectParamName), new NamedTargetExpression(fieldName));
				
			Expression<?> reflectiveCallInvocation = null;
			// Note that if the return type is a primitive, we first have to cast the primitive to its boxed variant, then cast to T
			try {
				Type type = advice().actualReturnType().getType();
				Java java = advice().language(Java.class);
				
				if (type.isTrue(java.property("primitive")))
					reflectiveCallInvocation = new ClassCastExpression(new BasicTypeReference<BasicTypeReference<?>>(java.box(type).getFullyQualifiedName()), getValueInvocation);
				else
					reflectiveCallInvocation = getValueInvocation;
				
				pc.parentLink().getOtherRelation().replace(pc.parentLink(), reflectiveCallInvocation.parentLink());
			} catch (LookupException e) {
				System.out.println("Error while getting advice type");
			}		
		}
		
		try {
			Type type = advice.actualReturnType().getType();
			if (type.signature().name().equals("void"))
				// We need an explicit return because the return type of the advice method is never 'void'
				adviceBody.addStatement(new ReturnStatement(new NullLiteral()));
			else {
				// We need to find all return statements and cast the return expression to the return type.
				// For instance in Java, the return type will be generic (T), so we need to cast to T. This isn't *always* necessary (e.g. return proceed())
				// but no harm is done if we do it anyway in those cases.
				for (AdviceReturnStatement st : adviceBody.descendants(AdviceReturnStatement.class)) {
					// Note that if the type is a primitive, we first have to cast the primitive to its boxed variant, then cast to T
					Java java = advice.language(Java.class);
					
					Expression<?> expressionToCast = null;
					if (type.isTrue(java.property("primitive")))
						expressionToCast = new ClassCastExpression(new BasicTypeReference<BasicTypeReference<?>>(java.box(type).getFullyQualifiedName()), st.getExpression());
					else
						expressionToCast = st.getExpression();

					ClassCastExpression cast = new ClassCastExpression(new BasicTypeReference<BasicTypeReference<?>>("T"), expressionToCast);
					st.setExpression(cast);
				}
			}
		} catch (LookupException e) {
			System.out.println("Error while getting advice type");
		}
		
		
		return adviceBody;
	}
}

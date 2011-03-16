package chameleon.aspects.advice.types.translation.fieldaccess;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.translation.ReflectiveAdviceTransformationProvider;
import chameleon.aspects.pointcut.expression.MatchResult;
import chameleon.core.expression.NamedTarget;
import chameleon.core.expression.NamedTargetExpression;
import chameleon.oo.type.BasicTypeReference;
import chameleon.oo.type.generics.BasicTypeArgument;
import chameleon.support.member.simplename.method.RegularMethodInvocation;

public abstract class ReflectiveFieldRead extends ReflectiveAdviceTransformationProvider {

	public ReflectiveFieldRead(MatchResult joinpoint) {
		super(joinpoint);
	}
	
	public final String fieldName = "_$field";

	public RegularMethodInvocation createGetFieldValueInvocation(NamedTarget aspectClassTarget, NamedTargetExpression objectTarget, NamedTargetExpression fieldNameTarget) {
		RegularMethodInvocation getFieldValueInvocation = new RegularMethodInvocation("getFieldValue", aspectClassTarget);
		getFieldValueInvocation.addArgument((new BasicTypeArgument(new BasicTypeReference("T"))));
		
		getFieldValueInvocation.addArgument(objectTarget);
		getFieldValueInvocation.addArgument(fieldNameTarget);
		
		return getFieldValueInvocation;
	}
	
	protected Advice advice;
	
	public Advice advice() {
		return this.advice;
	}
}

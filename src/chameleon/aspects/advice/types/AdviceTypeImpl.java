package chameleon.aspects.advice.types;

import chameleon.aspects.advice.Advice;
import chameleon.aspects.advice.types.translation.AdviceTranslationProvider;

/**
 * 	Represents a type of advice, e.g. After advice
 *
 */
public abstract class AdviceTypeImpl implements AdviceTranslationProvider {
	/**
	 * 	A text representation of this advice type
	 */
	private String name;
	
	private Advice advice;
	
	/**
	 * 	Constructor
	 * 
	 * 	@param 	name
	 * 			The text representation of this advice type
	 */
	public AdviceTypeImpl(String name, Advice advice) {
		this.name = name;
		this.advice = advice;
	}
	
	/**
	 * 	Get the text representation of this advice type
	 * 	
	 * 	@return The text representation
	 */
	public String getName() {
		return name;
	}
	
	public Advice advice() {
		return advice;
	}
}

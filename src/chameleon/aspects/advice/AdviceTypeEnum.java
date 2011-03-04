package chameleon.aspects.advice;

public enum AdviceTypeEnum {
	BEFORE("before"),
	AFTER("after"),
	AFTER_RETURNING("after-returning"),
	AFTER_THROWING("after-throwing"),
	AROUND("around");
	
	private String textual;
	private AdviceTypeEnum(String textual) {
		this.textual = textual;
	}
	
	@Override
	public String toString() {
		return textual;
	}
}

package chameleon.aspects.advice;

public enum AdviceType {
	BEFORE("before"),
	AFTER("after"),
	AFTER_RETURNING("after-returning"),
	AFTER_THROWING("after-throwing"),
	AROUND("around");
	
	private String textual;
	private AdviceType(String textual) {
		this.textual = textual;
	}
	
	@Override
	public String toString() {
		return textual;
	}
}

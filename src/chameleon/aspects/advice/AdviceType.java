package chameleon.aspects.advice;

public enum AdviceType {
	BEFORE("before"),
	AFTER("after"),
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

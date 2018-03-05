package exception;

public class customsException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String errorCode;
	
	public customsException(String massage) {
		super(massage);
	}
	
	public customsException(String errorCode, String massage) {
		super(massage);
		setErrorCode(errorCode);
		
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorCode() {
		return this.errorCode;
	}

}

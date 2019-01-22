package focus.corpus;

public class GithubException extends Exception {
	private static final long serialVersionUID = 1L;

	public GithubException(String message) {
		super(message);
	}
	
	public GithubException(String message, Throwable cause) {
		super(message, cause);
	}
}

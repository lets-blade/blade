package blade.kit.config.exception;

/**
 * 配置加载异常
 */
public class LoadException extends RuntimeException {

	private static final long serialVersionUID = 713951618562461059L;
	
	public LoadException(String msg){
		super(msg);
	}
	
	public LoadException(Throwable t){
		super(t);
	}
	
}
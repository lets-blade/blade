package blade.kit.logging;

public interface Logger {

    public void debug(String msg);
    
    public void debug(String msg, Object... args);
    
    public void info(String msg);
    
    public void info(String msg, Object... args);
    
    public void warn(String msg);
    
    public void warn(String msg, Object ...args);
    
    public void warn(String msg, Throwable exception);
    
    public void error(String msg);
    
    public void error(String msg, Object...args);
    
    public void error(String msg, Throwable t);
    
}
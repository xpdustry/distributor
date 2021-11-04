package fr.xpdustry.distributor.exception;

public class ScriptException extends Exception{
    public ScriptException(String message, Throwable cause){
        super(message, cause);
    }

    public ScriptException(Throwable cause){
        super(cause);
    }

    @Override
    public String getMessage(){
        return getCause().getClass().getSimpleName() + (getCause().getMessage() == null ? "" : ": " + getCause().getMessage());
    }
}

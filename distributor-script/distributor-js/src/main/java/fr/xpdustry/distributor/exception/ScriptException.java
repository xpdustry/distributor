package fr.xpdustry.distributor.exception;


import org.jetbrains.annotations.*;


/**
 * This class is a wrapper for rhino exceptions.
 */
public class ScriptException extends Exception{
    public ScriptException(final @NotNull Throwable cause){
        super(cause);
    }

    @Override public String getMessage(){
        return getCause().getClass().getSimpleName() +
            (getCause().getMessage() == null ? "" : ": " + getCause().getMessage());
    }
}

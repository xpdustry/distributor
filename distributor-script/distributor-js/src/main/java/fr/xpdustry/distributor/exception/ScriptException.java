package fr.xpdustry.distributor.exception;

import org.checkerframework.checker.nullness.qual.*;


/**
 * This class is a wrapper for rhino exceptions.
 */
public class ScriptException extends Exception{
    public ScriptException(@NonNull Throwable cause){
        super(cause);
    }

    @Override
    public String getMessage(){
        final var cause = (getCause().getMessage() == null ? "" : ": " + getCause().getMessage());
        return getCause().getClass().getSimpleName() + cause;
    }
}

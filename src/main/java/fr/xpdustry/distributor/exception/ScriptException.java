package fr.xpdustry.distributor.exception;

import org.jetbrains.annotations.*;


public class ScriptException extends Exception{
    public ScriptException(@NotNull Throwable cause){
        super(cause);
    }

    @Override
    public String getMessage(){
        return getCause().getClass().getSimpleName() + (getCause().getMessage() == null ? "" : ": " + getCause().getMessage());
    }
}

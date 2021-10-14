package fr.xpdustry.distributor.exception;

import fr.xpdustry.distributor.exception.type.*;

public class ParsingException extends PayloadException{
    private final ParsingExceptionType type;

    public ParsingException(ParsingExceptionType type){
        this.type = type;
    }

    public ParsingException(ParsingExceptionType type, Throwable cause){
        super(cause);
        this.type = type;
    }

    public ParsingExceptionType getType(){
        return type;
    }

    public ParsingException with(String key, Object value){
        return (ParsingException)super.with(key, value);
    }

}

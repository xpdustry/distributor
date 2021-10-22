package fr.xpdustry.distributor.exception;

import fr.xpdustry.distributor.exception.type.*;


public class ParsingException extends RuntimePayloadException{
    private final ParsingExceptionType type;

    public ParsingException(String message){
        super(message);
        this.type = ParsingExceptionType.UNDEFINED;
    }

    public ParsingException(ParsingExceptionType type){
        super();
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

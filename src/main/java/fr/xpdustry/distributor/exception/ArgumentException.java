package fr.xpdustry.distributor.exception;

public class ArgumentException extends PayloadException{
    private final ArgumentExceptionType type;

    public ArgumentException(ArgumentExceptionType type){
        this.type = type;
    }

    public ArgumentException(ArgumentExceptionType type, Throwable cause){
        super(cause);
        this.type = type;
    }

    public ArgumentExceptionType getType(){
        return type;
    }

    public ArgumentException with(String key, Object value){
        return (ArgumentException)super.with(key, value);
    }
}

package fr.xpdustry.distributor.exception;


public class ObjectStoreException extends RuntimeException{
    public ObjectStoreException(String message){
        super(message);
    }

    public ObjectStoreException(String message, Throwable cause){
        super(message, cause);
    }
}

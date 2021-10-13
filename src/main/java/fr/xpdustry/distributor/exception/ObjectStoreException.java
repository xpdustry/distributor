package fr.xpdustry.distributor.exception;


public class ObjectStoreException extends Exception{
    public ObjectStoreException(String message){
        super(message);
    }

    public ObjectStoreException(String message, Throwable cause){
        super(message, cause);
    }
}

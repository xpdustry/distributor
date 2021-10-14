package fr.xpdustry.distributor.exception.type;

import fr.xpdustry.distributor.util.struct.*;

import java.util.*;


public abstract class RuntimePayloadException extends RuntimeException implements ObjectStore{
    private final Map<String, Object> payload = new HashMap<>();

    public RuntimePayloadException(){
        /* Do something pls */
    }

    public RuntimePayloadException(String message){
        super(message);
    }

    public RuntimePayloadException(String message, Throwable cause){
        super(message, cause);
    }

    public RuntimePayloadException(Throwable cause){
        super(cause);
    }

    @Override
    public Object getObject(String key){
        return payload.get(key);
    }

    @Override
    public Object setObject(String key, Object value){
        return payload.put(key, value);
    }

    @Override
    public Object removeObject(String key){
        return payload.remove(key);
    }

    public Map<String, Object> getPayload(){
        return new HashMap<>(payload);
    }

    public RuntimePayloadException with(String key, Object value){
        setObject(key, value);
        return this;
    }
}

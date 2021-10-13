package fr.xpdustry.distributor.util.struct;

import arc.util.*;
import fr.xpdustry.distributor.exception.*;


public interface ObjectStore{
    @Nullable
    Object getObject(String key);

    @Nullable
    Object setObject(String key, Object value);

    @Nullable
    Object removeObject(String key);

    @Nullable
    @SuppressWarnings("unchecked")
    default <V> V getAs(String key) throws ObjectStoreException{
        try{
            return (V)getObject(key);
        }catch(ClassCastException e){
            throw new ObjectStoreException("The type of the object does not match the expected type.", e);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    default <V> V setAs(String key, V value) throws ObjectStoreException{
        try{
            return (V)setObject(key, value);
        }catch(ClassCastException e){
            throw new ObjectStoreException("The type of the object does not match the expected type.", e);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    default <V> V removeAs(String key) throws ObjectStoreException{
        try{
            return (V)removeObject(key);
        }catch(ClassCastException e){
            throw new ObjectStoreException("The type of the object does not match the expected type.", e);
        }
    }

    default boolean containsObject(String key){
        return getObject(key) != null;
    }
}

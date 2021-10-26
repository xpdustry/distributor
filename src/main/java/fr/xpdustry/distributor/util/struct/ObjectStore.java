package fr.xpdustry.distributor.util.struct;

import fr.xpdustry.distributor.exception.*;


public interface ObjectStore{
    Object getObject(String key);

    Object setObject(String key, Object value);

    Object removeObject(String key);

    @SuppressWarnings("unchecked")
    default <V> V getAs(String key) throws ObjectStoreException{
        try{
            return (V)getObject(key);
        }catch(ClassCastException e){
            throw new ObjectStoreException("The type of the object does not match the expected type.", e);
        }
    }

    @SuppressWarnings("unchecked")
    default <V> V setAs(String key, V value) throws ObjectStoreException{
        try{
            return (V)setObject(key, value);
        }catch(ClassCastException e){
            throw new ObjectStoreException("The type of the object does not match the expected type.", e);
        }
    }

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

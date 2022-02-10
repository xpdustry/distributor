package fr.xpdustry.distributor.struct;

import arc.struct.*;

import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;


/**
 * A {@link Set} view of a {@link ObjectSet}.
 */
public class ArcSet<E> extends AbstractSet<E>{
    private final ObjectSet<E> set;

    public ArcSet(@NotNull ObjectSet<E> set){
        this.set = set;
    }

    public ArcSet(int initial, float loadFactor){
        this(new ObjectSet<>(initial, loadFactor));
    }

    public ArcSet(int initial){
        this(new ObjectSet<>(initial));
    }

    public ArcSet(){
        this(new ObjectSet<>());
    }

    @Override public void forEach(@NotNull Consumer<? super E> action){
        set.forEach(action);
    }

    @Override public Iterator<E> iterator(){
        return set.iterator();
    }

    @Override public int size(){
        return set.size;
    }

    @Override public boolean isEmpty(){
        return set.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override public boolean contains(Object o){
        return set.contains((E)o);
    }

    @Override public Object[] toArray(){
        return set.asArray().toArray();
    }

    @Override public <T> T[] toArray(@NotNull T[] a){
        return set.asArray().toArray(a.getClass().getComponentType());
    }

    @Override public boolean add(E e){
        return set.add(e);
    }

    @SuppressWarnings("unchecked")
    @Override public boolean remove(Object o){
        return set.remove((E)o);
    }

    @Override public void clear(){
        set.clear();
    }
}

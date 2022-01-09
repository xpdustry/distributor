package fr.xpdustry.distributor.struct;

import arc.struct.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.function.*;


/**
 * A {@code List} view of a {@code Seq}.
 *
 * @param <E> the element type
 */
public class ArcList<E> extends AbstractList<E> implements RandomAccess{
    private final @NonNull Seq<E> seq;

    public ArcList(@NonNull Seq<E> seq){
        this.seq = seq;
    }

    public ArcList(int initial){
        this(new Seq<>(initial));
    }

    public ArcList(){
        this(new Seq<>());
    }


    @Override public void replaceAll(UnaryOperator<E> operator){
        seq.replace(operator::apply);
    }

    @Override public void sort(Comparator<? super E> c){
        seq.sort(c);
    }

    @Override public boolean removeIf(Predicate<? super E> filter){
        final var size = seq.size;
        return size != seq.removeAll(filter::test).size;
    }

    @Override public void forEach(Consumer<? super E> action){
        seq.forEach(action);
    }

    @Override public int size(){
        return seq.size;
    }

    @Override public boolean isEmpty(){
        return seq.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override public boolean contains(Object o){
        return seq.contains((E)o);
    }

    @Override public Object[] toArray(){
        return seq.toArray();
    }

    @Override public <T> T[] toArray(T[] a){
        return seq.toArray(a.getClass().getComponentType());
    }

    @SuppressWarnings("unchecked")
    @Override public boolean remove(Object o){
        return seq.remove((E)o);
    }

    @Override public boolean addAll(@NonNull Collection<? extends E> c){
        seq.addAll(c);
        return true;
    }

    @Override public boolean add(E e){
        seq.add(e);
        return true;
    }

    @Override public E get(int index){
        return seq.get(index);
    }

    @Override public E set(int index, E element){
        E old = seq.get(index);
        seq.set(index, element);
        return old;
    }

    @Override public void add(int index, E element){
        seq.insert(index, element);
    }

    @Override public E remove(int index){
        return seq.remove(index);
    }

    @SuppressWarnings("unchecked")
    @Override public int indexOf(Object o){
        return seq.indexOf((E)o);
    }

    @SuppressWarnings("unchecked")
    @Override public int lastIndexOf(Object o){
        return seq.lastIndexOf((E)o, false);
    }

    @Override public void clear(){
        seq.clear();
    }
}


















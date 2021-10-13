package fr.xpdustry.distributor.util.struct;

import java.util.*;
import java.util.function.*;


/** Holds a value and nothing else... */
public class Holder<T>{
    protected T value;

    public Holder(){
        this.value = null;
    }

    public Holder(T value){
        this.value = value;
    }

    public void set(T value){
        this.value = value;
    }

    public T get(){
        return value;
    }

    public void use(Consumer<T> cons){
        cons.accept(value);
    }

    public <R extends T> void compute(Function<T, R> func){
        value = func.apply(value);
    }

    @SuppressWarnings("unchecked")
    public <R> Holder<R> as(){
        return (Holder<R>)this;
    }

    @Override
    public String toString(){
        return value.toString();
    }

    @Override
    public boolean equals(Object o){
        return Objects.equals(value, o);
    }

    @Override
    public int hashCode(){
        return value != null ? value.hashCode() : 0;
    }

    public static Holder<Boolean> getBool(){
        return new Holder<>(false);
    }

    public static Holder<Byte> getByte(){
        return new Holder<>((byte)0);
    }

    public static Holder<Character> getChar(){
        return new Holder<>('\u0000');
    }

    public static Holder<Short> getShort(){
        return new Holder<>((short)0);
    }

    public static Holder<Integer> getInt(){
        return new Holder<>(0);
    }

    public static Holder<Long> getLong(){
        return new Holder<>(0L);
    }

    public static Holder<Float> getFloat(){
        return new Holder<>(0F);
    }

    public static Holder<Double> getDouble(){
        return new Holder<>(0D);
    }

    public static Holder<String> getString(){
        return new Holder<>("");
    }
}

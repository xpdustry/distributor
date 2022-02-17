package fr.xpdustry.distributor.struct;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.function.*;


/**
 * Holds a value and nothing else...
 */
public class Holder<T>{
    protected @Nullable T value;

    public Holder(){
        this.value = null;
    }

    public Holder(@Nullable T value){
        this.value = value;
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

    public void set(@Nullable T value){
        this.value = value;
    }

    public @Nullable T get(){
        return value;
    }

    public Holder<T> use(final @NonNull Consumer<T> cons){
        cons.accept(value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <R extends T> Holder<R> map(final @NonNull Function<T, R> func){
        value = func.apply(value);
        return (Holder<R>)this;
    }

    @SuppressWarnings("unchecked")
    public @NonNull <R> Holder<R> as(){
        return (Holder<R>)this;
    }

    @Override public int hashCode(){
        return Objects.hashCode(value);
    }

    @Override public boolean equals(@Nullable Object o){
        return (o instanceof Holder<?> h) && Objects.equals(value, h.value);
    }

    @Override public @NonNull String toString(){
        return String.valueOf(value);
    }
}

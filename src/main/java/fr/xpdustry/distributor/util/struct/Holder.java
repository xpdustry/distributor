package fr.xpdustry.distributor.util.struct;

import org.jetbrains.annotations.*;

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

    public void use(@NotNull Consumer<T> cons){
        cons.accept(value);
    }

    public <R extends T> void compute(@NotNull Function<T, R> func){
        value = func.apply(value);
    }

    @SuppressWarnings("unchecked")
    public @NotNull <R> Holder<R> as(){
        return (Holder<R>)this;
    }

    @Override
    public int hashCode(){
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(@Nullable Object o){
        if(o instanceof Holder){
            return Objects.equals(value, o);
        }else{
            return false;
        }
    }

    @Override
    public @NotNull String toString(){
        return String.valueOf(value);
    }
}

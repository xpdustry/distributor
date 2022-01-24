package fr.xpdustry.distributor.io.store;

import arc.files.*;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.util.function.*;


public class JsonFileStore<T> extends AbstractFileStore<T>{
    private final @NonNull Gson gson;

    public JsonFileStore(@NonNull Fi file, @NonNull Class<T> clazz, @NonNull Supplier<T> supplier, @NonNull Gson gson){
        super(file, clazz, supplier);
        this.gson = gson;
    }

    public JsonFileStore(@NonNull Fi file, @NonNull Class<T> clazz, @NonNull Supplier<T> supplier){
        this(file, clazz, supplier, new Gson());
    }

    @Override public void save(){
        try(final var writer = getFile().writer(false)){
            gson.toJson(get(), getObjectClass(), writer);
        }catch(IOException e){
            throw new RuntimeException("Unable to save the object at " + getFile(), e);
        }
    }

    @Override protected @NonNull T load(){
        try(final var reader = getFile().reader()){
            return gson.fromJson(reader, getObjectClass());
        }catch(IOException e){
            throw new RuntimeException("Unable to load the object at " + getFile(), e);
        }
    }
}

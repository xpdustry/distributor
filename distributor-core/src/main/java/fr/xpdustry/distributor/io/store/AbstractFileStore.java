package fr.xpdustry.distributor.io.store;

import arc.files.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


public abstract class AbstractFileStore<T> implements FileStore<T>{
    private final @NonNull Class<T> clazz;
    private @NonNull Fi file;
    private @NonNull T object;

    public AbstractFileStore(@NonNull Fi file, @NonNull Class<T> clazz, @NonNull Supplier<T> supplier){
        this.file = file;
        this.clazz = clazz;
        this.object = file.exists() ? load() : supplier.get();
    }

    @Override public @NonNull T get(){
        return object;
    }

    @Override public @NonNull Class<T> getObjectClass(){
        return clazz;
    }

    @Override public void set(@NonNull T object){
        this.object = object;
    }

    @Override public void reload(){
        this.object = load();
    }

    @Override public @NonNull Fi getFile(){
        return file;
    }

    @Override public void setFile(@NonNull Fi file){
        this.file = file;
    }

    protected abstract @NonNull T load();
}

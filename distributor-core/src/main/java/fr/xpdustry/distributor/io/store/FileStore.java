package fr.xpdustry.distributor.io.store;

import arc.files.*;

import org.checkerframework.checker.nullness.qual.*;


public interface FileStore<T>{
    @NonNull T get();

    @NonNull Class<T> getObjectClass();

    void set(@NonNull T object);

    void save();

    void reload();

    @NonNull Fi getFile();

    void setFile(@NonNull Fi file);
}

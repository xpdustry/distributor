package fr.xpdustry.distributor.io.store;

import arc.files.*;

import fr.xpdustry.distributor.io.*;

import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.util.*;
import java.util.function.*;


public class ConfigFileStore<T extends Accessible> extends AbstractFileStore<T>{
    private final @NonNull Factory factory;

    public ConfigFileStore(@NonNull Fi file, @NonNull Class<T> clazz, @NonNull Supplier<T> supplier, @NonNull Factory factory){
        super(file, clazz, supplier);
        this.factory = factory;
    }

    public ConfigFileStore(@NonNull Fi file, @NonNull Class<T> clazz, @NonNull Supplier<T> supplier){
        this(file, clazz, supplier, SingletonConfigFactory.getInstance());
    }

    public ConfigFileStore(@NonNull Fi file, @NonNull Class<T> clazz){
        this(file, clazz, () -> SingletonConfigFactory.getInstance().create(clazz));
    }

    public @NonNull Factory getFactory(){
        return factory;
    }

    @Override public void save(){
        try(final var out = getFile().write()){
            get().store(out, null);
        }catch(IOException e){
            throw new RuntimeException("Unable to save the config at " + getFile(), e);
        }
    }

    @Override protected @NonNull T load(){
        Properties properties = new Properties();

        if(getFile().exists()){
            try(final var reader = getFile().reader()){
                properties.load(reader);
            }catch(IOException e){
                throw new RuntimeException("Unable to load the config at " + getFile(), e);
            }
        }

        return factory.create(getObjectClass(), properties);
    }
}

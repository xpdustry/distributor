package fr.xpdustry.distributor.internal;

import arc.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public final class DistributorApplication implements ApplicationListener{
    private final Collection<Runnable> updateHooks = new HashSet<>();
    private final Collection<Runnable> shutdownHooks = new HashSet<>();

    public void addUpdateHook(@NonNull Runnable runnable){
        updateHooks.add(runnable);
    }

    public void addShutdownHook(@NonNull Runnable runnable){
        shutdownHooks.add(runnable);
    }

    public void removeUpdateHook(@NonNull Runnable runnable){
        updateHooks.remove(runnable);
    }

    public void removeShutdownHook(@NonNull Runnable runnable){
        shutdownHooks.remove(runnable);
    }

    @Override public void update(){
        updateHooks.forEach(Runnable::run);
    }

    @Override public void dispose(){
        shutdownHooks.forEach(Runnable::run);
    }
}

package fr.xpdustry.distributor.event;

import arc.func.*;

import org.checkerframework.checker.index.qual.*;
import org.checkerframework.checker.nullness.qual.*;


/**
 * This extension allows to give a lifetime to an event watcher.
 * If the lifetime reaches 0, the event watcher unregister itself.
 *
 * @param <T> the event type
 */
public class TimedEventWatcher<T> extends EventWatcher<T>{
    private int lifetime;

    public TimedEventWatcher(@NonNull Class<T> event, @NonNegative int lifetime, @NonNull Cons<T> listener){
        super(event, listener);
        this.lifetime = lifetime;
        if(lifetime < 0) throw new IllegalArgumentException("The lifetime cannot be below zero.");
    }

    public TimedEventWatcher(@NonNull T event, @NonNegative int lifetime, @NonNull Runnable listener){
        super(event, listener);
        this.lifetime = lifetime;
        if(lifetime < 0) throw new IllegalArgumentException("The lifetime cannot be below zero.");
    }

    @Override public void get(T o){
        if(lifetime > 0){
            super.get(o);
            lifetime--;
        }else{
            stop();
        }
    }

    public int getLifetime(){
        return lifetime;
    }

    public void setLifetime(@NonNegative int lifetime){
        this.lifetime = lifetime;
        if(lifetime < 0) throw new IllegalArgumentException("The lifetime cannot be below zero.");
    }
}

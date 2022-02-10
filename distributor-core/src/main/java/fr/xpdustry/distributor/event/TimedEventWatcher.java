package fr.xpdustry.distributor.event;

import arc.func.*;

import org.jetbrains.annotations.*;


/**
 * This extension allows to give a lifetime to an event watcher.
 * If the lifetime reaches 0, the event watcher unregister itself.
 *
 * @param <T> the event type
 */
public class TimedEventWatcher<T> extends EventWatcher<T>{
    private int lifetime;

    public TimedEventWatcher(
        final @NotNull Class<T> event,
        final int lifetime,
        final @NotNull Cons<T> listener
    ){
        super(event, listener);
        this.lifetime = lifetime;
    }

    public TimedEventWatcher(
        final @NotNull T event,
        final int lifetime,
        final @NotNull Runnable listener
    ){
        super(event, listener);
        this.lifetime = lifetime;
    }

    @Override public void get(final T event){
        if(lifetime > 0){
            super.get(event);
            lifetime--;
        }else{
            stop();
        }
    }

    public int getLifetime(){
        return lifetime;
    }

    public void setLifetime(final int lifetime){
        this.lifetime = lifetime;
    }
}

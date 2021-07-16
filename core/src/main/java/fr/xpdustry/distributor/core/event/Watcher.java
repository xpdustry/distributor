package fr.xpdustry.distributor.core.event;

import arc.func.*;


/**
 * Useless class ? As long as it compiles...
 * I have no idea what I am doing...
 */
public class Watcher<T>{
    public final Class<T> event;
    public final Cons<T> listener;

    protected Watcher(Class<T> event, Cons<T> listener){
        this.event = event;
        this.listener = listener;
    }

    public void trigger(T type){
        listener.get(type);
    }
}

package fr.xpdustry.distributor.core.event;

import arc.func.*;

import java.util.concurrent.atomic.*;


/**
 * Useless class ? As long as it compiles...
 * I have no idea what I am doing...
 */
public class Watcher<T>{
    public final Object event;
    public final Cons<T> listener;
    private final AtomicInteger lifetime;

    public Watcher(Class<T> event, Cons<T> listener){
        this(event, listener, -1);
    }

    public Watcher(T event, Cons<T> listener){
        this(event, listener, -1);
    }

    public Watcher(Class<T> event, Cons<T> listener, int lifetime){
        this.event = event;
        this.listener = listener;
        this.lifetime = new AtomicInteger(lifetime);
    }

    public Watcher(T event, Cons<T> listener, int lifetime){
        this.event = event;
        this.listener = listener;
        this.lifetime = new AtomicInteger(lifetime);
    }

    public void trigger(T type){
        System.out.println(lifetime);

        if(lifetime.get() == 0){
            PostMan.remove(this);
            return;
        }else if(lifetime.get() > 0){
            lifetime.decrementAndGet();
        }

        listener.get(type);
    }

    public int getLifetime(){
        return lifetime.get();
    }
}

package fr.xpdustry.distributor.event;

import java.util.concurrent.atomic.*;


public class Watcher<T>{
    private final Object event;
    private final EventListener<T> listener;
    private final AtomicInteger lifetime;

    public Watcher(Class<T> event, EventListener<T> listener){
        this(event, listener, -1);
    }

    public Watcher(Class<T> event, EventListener<T> listener, int lifetime){
        if(listener == null || event == null) throw new NullPointerException();

        this.event = event;
        this.listener = listener;
        this.lifetime = new AtomicInteger(lifetime);
    }

    public Watcher(T event, Runnable listener){
        this(event, listener, -1);
    }

    public Watcher(T event, Runnable listener, int lifetime){
        if(listener == null || event == null) throw new NullPointerException();

        this.event = event;
        this.listener = e -> listener.run();
        this.lifetime = new AtomicInteger(lifetime);
    }

    public void trigger(T type){
        if(lifetime.get() != 0){
            if(lifetime.get() > 0) lifetime.decrementAndGet();
            listener.trigger(type);
        }
    }

    public Object getEvent(){
        return event;
    }

    public int getLifetime(){
        return lifetime.get();
    }

    public void setLifetime(int lifetime){
        this.lifetime.set(lifetime);
    }
}

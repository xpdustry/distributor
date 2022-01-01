package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * A utility class to dynamically register/unregister an event listener from {@link Events}.
 *
 * @param <T> the class of the event to listen to
 */
public class EventWatcher<T> implements EventListener, Cons<T>{
    private static final ObjectMap<Object, Seq<Cons<?>>> events = Reflect.get(Events.class, "events");

    private final @NonNull Object event;
    private final @NonNull Cons<T> listener;
    private boolean listening = false;

    public EventWatcher(@NonNull Class<T> event, @NonNull Cons<T> listener){
        this.event = event;
        this.listener = listener;
    }

    public EventWatcher(@NonNull T event, @NonNull Runnable listener){
        this.event = event;
        this.listener = e -> listener.run();
    }

    public void listen(){
        if(!isListening()){
            events.get(event, Seq::new).add(this);
            listening = true;
        }
    }

    public void stop(){
        if(isListening()){
            events.get(event, Seq::new).remove(this);
            listening = false;
        }
    }

    public @NonNull Object getEvent(){
        return event;
    }

    public boolean isListening(){
        return listening;
    }

    @Override public void get(T o){
        listener.get(o);
    }
}

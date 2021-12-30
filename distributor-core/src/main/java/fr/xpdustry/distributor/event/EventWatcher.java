package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * A utility class to dynamically register/unregister an event listener from {@link Events}.
 *
 * @param <T> the class of the event to listen to
 */
public class EventWatcher<T> implements EventListener{
    private static final ObjectMap<Object, Seq<Cons<?>>> events = Reflect.get(Events.class, "events");

    private final @NonNull Object event;
    private final @NonNull Cons<T> listener;
    private boolean listening = false;

    public EventWatcher(@NonNull Class<T> event, @NonNull Cons<T> listener){
        this.event = requireNonNull(event, "event can't be null.");
        this.listener = requireNonNull(listener, "listener can't be null.");
    }

    public EventWatcher(@NonNull T event, @NonNull Runnable listener){
        requireNonNull(listener, "listener can't be null.");
        this.event = requireNonNull(event, "event can't be null.");
        this.listener = e -> listener.run();
    }

    public void listen(){
        if(!isListening()){
            events.get(event, Seq::new).add(listener);
            listening = true;
        }
    }

    public void stop(){
        if(isListening()){
            events.get(event, Seq::new).remove(listener);
            listening = false;
        }
    }

    protected void trigger(@NonNull T type){
        listener.get(type);
    }

    public @NonNull Object getEvent(){
        return event;
    }

    public boolean isListening(){
        return listening;
    }
}

package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * A utility class to dynamically register/unregister an event listener from {@link Events}.
 * @param <T> the class of the event to listen to
 */
public class EventWatcher<T> implements EventListener{
    private final @NonNull Class<T> event;
    private final @NonNull Cons<T> listener;
    private boolean listening = false;

    public EventWatcher(@NonNull Class<T> event, @NonNull Cons<T> listener){
        this.event = requireNonNull(event, "event can't be null.");
        this.listener = requireNonNull(listener, "listener can't be null.");
    }

    public void listen(){
        if(!isListening()){
            Events.on(event, listener);
            listening = true;
        }
    }

    public void stop(){
        if(isListening()){
            Events.remove(event, listener);
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

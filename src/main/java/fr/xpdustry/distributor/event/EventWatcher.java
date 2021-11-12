package fr.xpdustry.distributor.event;

import arc.*;
import arc.func.*;

import org.jetbrains.annotations.*;

import java.util.*;


/**
 * A utility class to dynamically register/unregister an event listener from {@link Events}.
 * @param <T> the class of the event to listen to
 */
public class EventWatcher<T> implements EventListener{
    private final @NotNull Class<T> event;
    private final @NotNull Cons<T> listener;
    private boolean listening = false;

    public EventWatcher(@NotNull Class<T> event, @NotNull Cons<T> listener){
        this.event = event;
        this.listener = listener;
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

    protected void trigger(T type){
        listener.get(type);
    }

    public Object getEvent(){
        return event;
    }

    public boolean isListening(){
        return listening;
    }
}

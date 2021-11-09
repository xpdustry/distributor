package fr.xpdustry.distributor.util;

import arc.*;
import arc.func.*;

import java.util.*;


public class EventWatcher<T> implements EventListener{
    private final Class<T> event;
    private final Cons<T> listener;
    private boolean listening = false;

    public EventWatcher(Class<T> event, Cons<T> listener){
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

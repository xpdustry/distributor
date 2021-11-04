package fr.xpdustry.distributor.event;

import arc.*;

import java.util.*;
import java.util.concurrent.*;


@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class PostMan{
    private static final Set<Object> arcEvents = new HashSet<>(8);
    private static final Map<Object, Set<EventWatcher<?>>> events = new ConcurrentHashMap<>(32);

    private PostMan(){
        /* Why would you do that? */
    }

    public static <T> void bind(Class<T> event){
        synchronized(arcEvents){
            if(!arcEvents.contains(event)){
                Events.on(event, PostMan::fire);
                arcEvents.add(event);
            }
        }
    }

    public static <T> void bind(T event){
        synchronized(arcEvents){
            if(!arcEvents.contains(event)){
                Events.run(event, () -> PostMan.fire(event));
                arcEvents.add(event);
            }
        }
    }

    public static <T> EventWatcher<T> on(Class<T> type, EventRunner<T> runner){
        return on(new EventWatcher<>(type, runner));
    }

    public static <T> EventWatcher<T> on(T type, Runnable listener){
        return on(new EventWatcher<>(type, listener));
    }

    public static <T> EventWatcher<T> on(EventWatcher<T> listener){
        events.computeIfAbsent(listener.getEvent(), key -> ConcurrentHashMap.newKeySet(8)).add(listener);
        return listener;
    }

    public static <T> boolean contains(EventWatcher<T> listener){
        Set<EventWatcher<?>> set = events.get(listener.getEvent());
        if(set != null) return set.contains(listener);
        else return false;
    }

    public static <T> void remove(EventWatcher<T> listener){
        Set<EventWatcher<?>> set = events.get(listener.getEvent());
        if(set != null) set.remove(listener);
    }

    public static <T> void fire(T type){
        fire(type.getClass(), type);
    }

    @SuppressWarnings("unchecked")
    public static <T> void fire(Class<?> ctype, T type){
        if(events.containsKey(type)) events.get(type).forEach(listener -> ((EventWatcher<T>)listener).trigger(type));
        if(events.containsKey(ctype)) events.get(ctype).forEach(listener -> ((EventWatcher<T>)listener).trigger(type));
    }
}

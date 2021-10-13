package fr.xpdustry.distributor.event;

import arc.*;

import java.util.*;
import java.util.concurrent.*;


@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class PostMan{
    private static final Set<Object> arcEvents = new HashSet<>(8);

    private static final Map<Object, Set<Watcher<?>>> events = new ConcurrentHashMap<>(64);

    private PostMan(){
        /* Why would you do that ? */
    }

    public static synchronized <T> void bind(Class<T> event){
        if(!arcEvents.contains(event)){
            Events.on(event, PostMan::fire);
            arcEvents.add(event);
        }
    }

    public static synchronized <T> void bind(T event){
        if(!arcEvents.contains(event)){
            Events.run(event, () -> PostMan.fire(event));
            arcEvents.add(event);
        }
    }

    public static <T> Watcher<T> on(Class<T> type, EventListener<T> listener){
        return on(new Watcher<>(type, listener));
    }

    public static <T> Watcher<T> on(Class<T> type, EventListener<T> listener, int lifetime){
        return on(new Watcher<>(type, listener, lifetime));
    }

    public static <T> Watcher<T> on(T type, Runnable listener){
        return on(new Watcher<>(type, listener));
    }

    public static <T> Watcher<T> on(T type, Runnable listener, int lifetime){
        return on(new Watcher<>(type, listener, lifetime));
    }

    public static <T> Watcher<T> on(Watcher<T> watcher){
        events.computeIfAbsent(watcher.getEvent(), key -> ConcurrentHashMap.newKeySet(8)).add(watcher);
        return watcher;
    }

    public static <T> boolean contains(Watcher<T> watcher){
        Set<Watcher<?>> set = events.get(watcher.getEvent());
        if(set != null) return set.contains(watcher);
        else return false;
    }

    public static <T> void remove(Watcher<T> watcher){
        Set<Watcher<?>> set = events.get(watcher.getEvent());
        if(set != null) set.remove(watcher);
    }

    public static <T> void fire(T type){
        fire(type, EventRunner.DEFAULT);
    }

    public static <T> void fire(T type, EventRunner runner){
        fire(type.getClass(), type, runner);
    }

    public static <T> void fire(Class<?> ctype, T type){
        fire(ctype, type, EventRunner.DEFAULT);
    }

    @SuppressWarnings("unchecked")
    public static <T> void fire(Class<?> ctype, T type, EventRunner runner){
        if(events.containsKey(type)){
            runner.run(() -> events.get(type).forEach(listener -> ((Watcher<T>)listener).trigger(type)));
        }if(events.containsKey(ctype)){
            runner.run(() -> events.get(ctype).forEach(listener -> ((Watcher<T>)listener).trigger(type)));
        }
    }
}































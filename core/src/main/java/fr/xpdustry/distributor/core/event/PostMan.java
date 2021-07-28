package fr.xpdustry.distributor.core.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.Timer;
import mindustry.content.*;

import java.util.*;
import java.util.concurrent.*;


/**
 * A non blocking event pool I guess...
 */
public final class PostMan{
    private static final Set<Object> arcEvents = new HashSet<>(8);
    private static final Map<Object, Set<Watcher<?>>> postManEvents =
        new ConcurrentHashMap<>(32);

    public static synchronized <T> void bind(Class<T> event){
        if(!arcEvents.contains(event)){
            Events.on(event, PostMan::fire);
            arcEvents.add(event);
        }
    }

    public static <T> Watcher<T> on(T type, Cons<T> listener){
        return on(new Watcher<>(type, listener));
    }

    public static <T> Watcher<T> on(Class<T> type, Cons<T> listener){
        return on(new Watcher<>(type, listener));
    }

    public static <T> Watcher<T> on(T type, Cons<T> listener, int lifetime){
        return on(new Watcher<>(type, listener, lifetime));
    }

    public static <T> Watcher<T> on(Class<T> type, Cons<T> listener, int lifetime){
        return on(new Watcher<>(type, listener, lifetime));
    }

    public static <T> Watcher<T> on(Watcher<T> watcher){
        Set<Watcher<?>> set = postManEvents.computeIfAbsent(
            watcher.event, key -> ConcurrentHashMap.newKeySet());

        set.add(watcher);
        return watcher;
    }

    public static <T> void remove(Watcher<T> watcher){
        Set<Watcher<?>> set = postManEvents.get(watcher.event);
        if(set != null) set.remove(watcher);
    }

    public static <T> void fire(T type){
        fire(type, EventRunner.defaultRunner);
    }

    public static <T> void fire(T type, EventRunner runner){
        fire(type.getClass(), type, runner);
    }

    public static <T> void fire(Class<?> ctype, T type){
        fire(ctype, type, EventRunner.defaultRunner);
    }

    @SuppressWarnings("unchecked")
    public static <T> void fire(Class<?> ctype, T type, EventRunner runner){
        if(postManEvents.containsKey(type)){
            runner.get(() -> postManEvents.get(type).forEach(e -> ((Watcher<T>)e).trigger(type)));
        }if(postManEvents.containsKey(ctype)){
            runner.get(() -> postManEvents.get(ctype).forEach(e -> ((Watcher<T>)e).trigger(type)));
        }
    }
}































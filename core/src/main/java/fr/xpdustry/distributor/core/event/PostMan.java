package fr.xpdustry.distributor.core.event;

import arc.*;
import arc.func.*;
import arc.struct.*;

import java.util.concurrent.*;


/**
 * A non blocking event pool I guess...
 */
public final class PostMan{
    private static final ObjectSet<Object> arcEvents = new ObjectSet<>();
    private static final ObjectMap<Object, ObjectSet<Watcher<?>>> postManEvents = new ObjectMap<>();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static <T> void bind(Class<T> type){
        if(!arcEvents.contains(type)){
            Events.on(type, PostMan::fire);
            arcEvents.add(type);
        }
    }

    public static void bind(Class<?>... types){
        bind(Seq.with(types));
    }

    public static void bind(Seq<Class<?>> types){
        Seq<Class<?>> filter = types.select(type -> !arcEvents.contains(type));
        filter.forEach(type -> Events.on(type, PostMan::fire));
        arcEvents.addAll(types);
    }

    public static <T> Watcher<T> on(Watcher<T> watcher){
        postManEvents.get(watcher.event, ObjectSet::new).add(watcher);
        return watcher;
    }

    public static <T> Watcher<T> on(Class<T> type, Cons<T> listener){
        Watcher<T> watcher = new Watcher<>(type, listener);
        postManEvents.get(type, ObjectSet::new).add(watcher);
        return watcher;
    }

    public static <T> void remove(Watcher<T> watcher){
        postManEvents.get(watcher.event, ObjectSet::new).remove(watcher);
    }

    public static <T> void fire(T type){
        fire(type.getClass(), type);
    }

    @SuppressWarnings("unchecked")
    public static <T> void fire(Class<?> ctype, T type){
        if(postManEvents.containsKey(type)){
            executor.submit(() -> postManEvents.get(type).each(e -> ((Watcher<T>)e).trigger(type)));
        }if(postManEvents.containsKey(ctype)){
            executor.submit(() -> postManEvents.get(ctype).each(e -> ((Watcher<T>)e).trigger(type)));
        }
    }
}

package fr.xpdustry.distributor.event;

import arc.*;

import fr.xpdustry.distributor.struct.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;


public class TimedEventWatcherTest{
    private Object event;
    private Holder<Integer> holder;

    @BeforeEach
    public void setup(){
        event = new Object();
        holder = Holder.getInt();
    }

    @ParameterizedTest
    @ValueSource(strings = {"CONS", "RUNNABLE"})
    public void test_event_fire(String type){
        final var watcher = getWatcher(type, 2);

        Events.fire(event);
        assertEquals(0, holder.get());
        assertEquals(2, watcher.getLifetime());

        watcher.listen();
        assertEquals(0, holder.get());
        assertEquals(2, watcher.getLifetime());

        Events.fire(event);
        assertEquals(1, holder.get());
        assertEquals(1, watcher.getLifetime());

        Events.fire(event);
        assertEquals(2, holder.get());
        assertEquals(0, watcher.getLifetime());

        Events.fire(event);
        assertEquals(2, holder.get());
        assertEquals(0, watcher.getLifetime());
        assertFalse(watcher.isListening());
    }

    @SuppressWarnings("ConstantConditions")
    public TimedEventWatcher<Object> getWatcher(String type, int lifetime){
        return switch(type){
            case "CONS" -> new TimedEventWatcher<>(Object.class, lifetime, o -> holder.set(holder.get() + 1));
            case "RUNNABLE" -> new TimedEventWatcher<>(event, lifetime, () -> holder.set(holder.get() + 1));
            default -> throw new IllegalArgumentException("Unable to resolve constructor: " + type);
        };
    }
}

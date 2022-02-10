package fr.xpdustry.distributor.event;

import arc.*;

import fr.xpdustry.distributor.struct.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;


public class EventWatcherTest{
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
        final var watcher = getWatcher(type);

        Events.fire(event);
        assertEquals(0, holder.get());
        assertFalse(watcher.isListening());

        watcher.listen();
        assertEquals(0, holder.get());
        assertTrue(watcher.isListening());

        Events.fire(event);
        assertEquals(1, holder.get());
        assertTrue(watcher.isListening());

        watcher.stop();
        Events.fire(event);
        assertFalse(watcher.isListening());
        assertEquals(1, holder.get());
    }


    @SuppressWarnings("ConstantConditions")
    public EventWatcher<Object> getWatcher(String type){
        return switch(type){
            case "CONS" -> new EventWatcher<>(Object.class, o -> holder.set(holder.get() + 1));
            case "RUNNABLE" -> new EventWatcher<>(event, () -> holder.set(holder.get() + 1));
            default -> throw new IllegalArgumentException("Unable to resolve constructor: " + type);
        };
    }
}

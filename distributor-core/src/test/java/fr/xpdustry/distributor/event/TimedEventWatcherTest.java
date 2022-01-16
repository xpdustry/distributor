package fr.xpdustry.distributor.event;

import arc.*;

import fr.xpdustry.distributor.struct.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class TimedEventWatcherTest{
    @Test
    public void test_event_fire(){
        final var executed = Holder.getInt().map(i -> 2);
        final var event = new TimedEventWatcher<>(TimedEventWatcherTest.class, 2, e -> executed.map(i -> i - 1));

        Events.fire(this);
        assertEquals(2, executed.get());
        assertEquals(2, event.getLifetime());

        event.listen();
        assertEquals(2, executed.get());
        assertEquals(2, event.getLifetime());

        Events.fire(this);
        assertEquals(1, executed.get());
        assertEquals(1, event.getLifetime());

        Events.fire(this);
        assertEquals(0, executed.get());
        assertEquals(0, event.getLifetime());

        Events.fire(this);
        assertEquals(0, executed.get());
        assertEquals(0, event.getLifetime());
        assertFalse(event.isListening());
    }

    @Test
    public void test_throw_on_negative_lifetime(){
        assertThrows(IllegalArgumentException.class, () -> new TimedEventWatcher<>(Object.class, -10, e -> {}));
        final var event = new TimedEventWatcher<>(Object.class, 10, e -> {});
        assertThrows(IllegalArgumentException.class, () -> event.setLifetime(-3));
    }
}

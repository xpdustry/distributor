package fr.xpdustry.distributor.event;

import arc.*;

import fr.xpdustry.distributor.struct.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class EventWatcherTest{
    @Test
    public void test_event_fire(){
        final var executed = Holder.getBool();
        final var event = new EventWatcher<>(EventWatcherTest.class, e -> executed.set(true));

        Events.fire(this);
        assertEquals(Boolean.FALSE, executed.get());
        assertFalse(event.isListening());

        event.listen();
        assertEquals(Boolean.FALSE, executed.get());
        assertTrue(event.isListening());

        Events.fire(this);
        assertEquals(Boolean.TRUE, executed.get());
        assertTrue(event.isListening());

        event.stop();
        Events.fire(this);
        assertFalse(event.isListening());
    }
}

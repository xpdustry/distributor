package fr.xpdustry.distributor.event;

import arc.*;

import mindustry.game.EventType.*;

import fr.xpdustry.distributor.struct.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class EventWatcherTest{
    @Test
    public void test_event_fire(){
        final var executed = Holder.getBool();
        final var event = new EventWatcher<>(Trigger.update, () -> executed.set(true));
        event.listen();

        assertEquals(Boolean.FALSE, executed.get());
        Events.fire(Trigger.update);
        assertEquals(Boolean.TRUE, executed.get());
    }
}

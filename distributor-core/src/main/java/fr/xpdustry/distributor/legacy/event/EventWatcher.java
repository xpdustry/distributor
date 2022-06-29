package fr.xpdustry.distributor.legacy.event;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * A utility class for dynamically register/unregister an event listener from {@link Events}.
 *
 * @param <T> the event type
 */
public class EventWatcher<T> implements EventListener, Cons<T> {

  private static final ObjectMap<Object, Seq<Cons<?>>> events = Reflect.get(Events.class, "events");

  private final Object event;
  private final Cons<T> listener;
  private boolean listening = false;

  public EventWatcher(final @NotNull Class<T> event, final @NotNull Cons<T> listener) {
    this.event = event;
    this.listener = listener;
  }

  public EventWatcher(final @NotNull T event, final @NotNull Runnable listener) {
    this.event = event;
    this.listener = e -> listener.run();
  }

  public void listen() {
    if (!isListening()) {
      events.get(event, Seq::new).add(this);
      listening = true;
    }
  }

  public void stop() {
    if (isListening()) {
      events.get(event, Seq::new).remove(this);
      listening = false;
    }
  }

  public @NotNull Object getEvent() {
    return event;
  }

  public @NotNull Cons<T> getListener() {
    return listener;
  }

  public boolean isListening() {
    return listening;
  }

  @Override
  public void get(final T event) {
    listener.get(event);
  }
}

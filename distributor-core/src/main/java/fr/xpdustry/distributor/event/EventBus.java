package fr.xpdustry.distributor.event;

public interface EventBus {

  static EventBus mindustry() {
    return MindustryEventBus.INSTANCE;
  }

  <E> void post(final E event);

  <E> void register(final Class<E> event, final EventListener<E> listener);

  void register(final Object listener);

  <E> void unregister(final Class<E> event, final EventListener<E> listener);

  void unregister(final Object listener);
}

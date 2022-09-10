package fr.xpdustry.distributor.event;

public interface EventSubscriber<E> {

  void onEvent(final E event);

  default EventPriority getPriority() {
    return EventPriority.NORMAL;
  }
}

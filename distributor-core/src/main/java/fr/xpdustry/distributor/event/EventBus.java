package fr.xpdustry.distributor.event;

public interface EventBus {

  static EventBus mindustry() {
    return ArcEventBus.INSTANCE;
  }

  EventPostResult post(final Object event);

  <E> EventSubscription subscribe(final Class<E> event, final EventSubscriber<E> subscriber);

  EventSubscription subscribe(final Object object);
}

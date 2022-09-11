package fr.xpdustry.distributor.event;

public interface EventBus {

  static EventBus mindustry() {
    return ArcEventBus.INSTANCE;
  }

  EventPostResult post(final Object event);

  void register(final Object object);

  void unregister(final Object object);
}

package fr.xpdustry.distributor.event;

import org.jetbrains.annotations.*;

public interface EventBus {

  static @NotNull EventBus mindustry() {
    return ArcEventBus.INSTANCE;
  }

  void post(final @NotNull Object event);

  void register(final @NotNull Object object);

  void unregister(final @NotNull Object object);
}

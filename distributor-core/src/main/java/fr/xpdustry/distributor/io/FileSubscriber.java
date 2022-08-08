package fr.xpdustry.distributor.io;

import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.Flow.*;

@FunctionalInterface
public interface FileSubscriber extends Flow.Subscriber<WatchEvent<?>> {

  @Override
  default void onSubscribe(final Subscription subscription) {
    subscription.request(Long.MAX_VALUE);
  }

  @Override
  default void onError(Throwable throwable) {
    final var thread = Thread.currentThread();
    thread.getUncaughtExceptionHandler().uncaughtException(thread, throwable);
  }

  @Override
  default void onComplete() {
  }
}

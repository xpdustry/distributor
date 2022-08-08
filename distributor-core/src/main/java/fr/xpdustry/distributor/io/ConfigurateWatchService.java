package fr.xpdustry.distributor.io;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.reactive.*;
import org.spongepowered.configurate.reference.*;
import org.checkerframework.checker.nullness.qual.*;

final class ConfigurateWatchService implements FileWatchService {

  private final WatchServiceListener service;

  {
    try {
      this.service = WatchServiceListener.create();
    } catch (final IOException e) {
      throw new RuntimeException("Failed to create the service.", e);
    }
  }

  @Override
  public void subscribeToFile(Path path, FileSubscriber subscriber) {
    try {
      final ConfigurateAdapter adapter = new ConfigurateAdapter(subscriber);
      adapter.disposable = service.listenToFile(path, adapter);
    } catch (final ConfigurateException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void subscribeToDirectory(Path path, FileSubscriber subscriber) {
    try {
      final ConfigurateAdapter adapter = new ConfigurateAdapter(subscriber);
      adapter.disposable = service.listenToDirectory(path, adapter);
    } catch (final ConfigurateException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws IOException {
    service.close();
  }

  private static final class ConfigurateAdapter implements Subscriber<WatchEvent<?>>, Flow.Subscription {

    private final FileSubscriber subscriber;
    private @MonotonicNonNull Disposable disposable;

    private boolean unbounded = false;
    private long requests = 0;
    private boolean cancelled = false;

    private ConfigurateAdapter(final FileSubscriber subscriber) {
      this.subscriber = subscriber;
    }

    @Override
    public void submit(final WatchEvent<?> item) {
      if (!cancelled) {
        if (unbounded) {
          subscriber.onNext(item);
        } else if (requests > 0) {
          subscriber.onNext(item);
          requests -= 1;
        }
      }
    }

    @Override
    public void onError(Throwable thrown) {
      subscriber.onError(thrown);
    }

    @Override
    public void onClose() {
      subscriber.onComplete();
    }

    @Override
    public void request(long n) {
      if (!unbounded) {
        if (n == Long.MAX_VALUE) {
          unbounded = true;
        } else if (n <= 0) {
          throw new IllegalArgumentException("n is below 0: " + n);
        } else {
          requests += n;
        }
      }
    }

    @Override
    public void cancel() {
      if (!cancelled && disposable != null) {
        disposable.dispose();
      }
      cancelled = true;
    }
  }
}

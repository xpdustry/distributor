package fr.xpdustry.distributor.io;

import java.io.*;
import java.nio.file.*;

public interface FileWatchService extends Closeable {

  static FileWatchService create() {
    return new ConfigurateWatchService();
  }

  void subscribeToFile(final Path path, final FileSubscriber subscriber);

  void subscribeToDirectory(final Path path, final FileSubscriber subscriber);
}

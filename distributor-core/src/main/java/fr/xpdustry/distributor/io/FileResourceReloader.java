package fr.xpdustry.distributor.io;

import arc.util.*;
import java.io.*;
import java.nio.file.*;

public class FileResourceReloader implements FileSubscriber {

  private final FileResource resource;

  public FileResourceReloader(final FileResource resource) {
    this.resource = resource;
  }

  @Override
  public void onNext(final WatchEvent<?> item) {
    final var context = (Path) item.context();
    if (context.equals(this.resource.getPath())) {
      try {
        this.resource.load();
      } catch (IOException e) {
        Log.err("Failed to reload the file resource at " + resource.getPath(), e);
      }
    }
  }
}

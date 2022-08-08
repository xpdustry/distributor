package fr.xpdustry.distributor.io;

import java.nio.file.*;

public interface FileResource extends PluginResource {

  Path getPath();
}

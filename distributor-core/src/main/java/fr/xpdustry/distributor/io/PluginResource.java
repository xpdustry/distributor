package fr.xpdustry.distributor.io;

import java.io.*;

public interface PluginResource {

  void load() throws IOException;

  void save() throws IOException;
}

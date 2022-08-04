package fr.xpdustry.distributor.plugin;

import java.io.*;

public interface PluginResource {

  void load() throws IOException;

  void save() throws IOException;
}

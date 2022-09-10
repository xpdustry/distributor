package fr.xpdustry.distributor.ui;

import java.io.*;

public interface CloseableView<P extends Pane> extends View<P>, Closeable {

  @Override
  void close();
}

package fr.xpdustry.distributor.ui;

import java.net.*;
import java.util.function.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

@FunctionalInterface
public interface Action<P extends Pane> extends Consumer<View<P>> {

  static <P extends Pane> Action<P> none() {
    return view -> {};
  }

  static <P extends Pane> Action<P> reopen() {
    return view -> view.getInterface().open(view.getViewer(), view.getMetadata());
  }

  static <P extends Pane> Action<P> uri(final @NotNull URI uri) {
    return view -> Call.openURI(uri.toString());
  }

  @Override
  void accept(final View<P> view);
}

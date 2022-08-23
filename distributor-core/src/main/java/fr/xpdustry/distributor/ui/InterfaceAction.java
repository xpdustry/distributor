package fr.xpdustry.distributor.ui;

import java.util.function.*;
import mindustry.gen.*;

@FunctionalInterface
public interface InterfaceAction<I extends Interface> extends BiConsumer<Player, I> {

  static <I extends Interface> InterfaceAction<I> nothing() {
    return (player, inter) -> {};
  }

  static <I extends Interface> InterfaceAction<I> reopen() {
    return (player, inter) -> inter.show(player);
  }

  /*
  For v138
  static <I extends Interface> InterfaceAction<I> uri(final URI uri) {
    return (player, inter) -> Call.openURI(uri.toString());
  }
   */

  @Override
  void accept(final Player player, final I inter);
}

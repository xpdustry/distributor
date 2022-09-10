package fr.xpdustry.distributor.ui;

import java.util.function.*;

@FunctionalInterface
public interface Transformer<P extends Pane> extends Function<View<P>, P> {

  @Override
  P apply(final View<P> view);
}

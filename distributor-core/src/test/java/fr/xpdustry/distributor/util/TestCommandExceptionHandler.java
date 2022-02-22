package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.command.sender.ArcCommandSender;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;

public class TestCommandExceptionHandler<E extends Throwable> implements BiConsumer<ArcCommandSender, E> {

  private @Nullable E lastException = null;

  @Override
  public void accept(ArcCommandSender sender, E e) {
    this.lastException = e;
  }

  public @Nullable E getLastException() {
    return lastException;
  }
}

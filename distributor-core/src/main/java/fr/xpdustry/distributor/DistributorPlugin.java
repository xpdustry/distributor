package fr.xpdustry.distributor;

import fr.xpdustry.distributor.internal.*;
import java.util.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;

public class DistributorPlugin extends Plugin {

  private static final DistributorRuntime runtime;

  static {
    final var loader = ServiceLoader.load(
      DistributorRuntime.class,
      DistributorPlugin.class.getClassLoader()
    );
    final var iterator = loader.iterator();
    if (iterator.hasNext()) {
      runtime = iterator.next();
      if (iterator.hasNext()) {
        throw new IllegalStateException("Found more than one DistributorRuntime implementation!");
      }
    } else {
      throw new IllegalStateException("No DistributorRuntime implementation was found!");
    }
  }

  public static @NotNull DistributorRuntime getRuntime() {
    return runtime;
  }

  @Override
  public void init() {
  }
}

package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.plugin.*;
import java.lang.reflect.*;
import mindustry.mod.*;

public final class Magik {

  public static String getPluginNamespace(final Plugin plugin) {
    return plugin instanceof ExtendedPlugin extended
      ? extended.getDescriptor().getName()
      : PluginDescriptor.from(plugin).getName();
  }

  public static <T> T[] removeElementFromArray(final T[] array, final int index) {
    if (index < 0 || index >= array.length) {
      throw new IllegalArgumentException("The index is out of bounds: " + index);
    }
    @SuppressWarnings("unchecked")
    final var copy = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - 1);
    for (int i = 0, j = 0; i < array.length; i++) {
      if (i != index) {
        copy[j++] = array[i];
      }
    }
    return copy;
  }

  private Magik() {
  }
}

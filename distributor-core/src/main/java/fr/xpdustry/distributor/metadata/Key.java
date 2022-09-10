package fr.xpdustry.distributor.metadata;

import io.leangen.geantyref.*;
import java.util.*;
import org.jetbrains.annotations.*;

public final class Key<V> {

  private final TypeToken<V> valueType;
  private final String name;

  private Key(final TypeToken<V> valueType, final String name) {
    this.valueType = valueType;
    this.name = name;
  }

  public static <V> Key<V> of(final Class<V> type, final String namespace, final String name) {
    return new Key<>(TypeToken.get(type), namespace + ":" + name);
  }

  public static <V> Key<V> of(final Class<V> type, final String name) {
    return new Key<>(TypeToken.get(type), name);
  }

  public static <V> Key<V> of(final TypeToken<V> type, final String namespace, final String name) {
    return new Key<>(type, namespace + ":" + name);
  }

  public static <V> Key<V> of(final TypeToken<V> type, final String name) {
    return new Key<>(type, name);
  }

  public TypeToken<V> getValueType() {
    return valueType;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(final @Nullable Object o) {
    return this == o || (o instanceof Key<?> key && this.valueType.equals(key.valueType) && this.name.equals(key.name));
  }

  @Override
  public int hashCode() {
    return Objects.hash(valueType, name);
  }

  @Override
  public String toString() {
    return name;
  }
}

package fr.xpdustry.distributor.data;

import io.leangen.geantyref.*;
import java.util.*;

public final class Key<V> implements Namespaced {

  private final TypeToken<V> valueType;
  private final String namespace;
  private final String name;

  private Key(final TypeToken<V> valueType, final String namespace, final String name) {
    this.valueType = valueType;
    this.namespace = namespace;
    this.name = name;
  }

  public static <V> Key<V> of(final Class<V> type, final String namespace, final String name) {
    return new Key<>(TypeToken.get(type), namespace, name);
  }

  public static <V> Key<V> of(final TypeToken<V> type, final String namespace, final String name) {
    return new Key<>(type, namespace, name);
  }

  public TypeToken<V> getValueType() {
    return valueType;
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public String getNamespacedName() {
    return namespace + ":" + name;
  }

  @Override
  public boolean equals(final Object o) {
    return this == o || (
      o instanceof Key<?> key
        && this.valueType.equals(key.valueType)
        && this.namespace.equals(key.namespace)
        && this.name.equals(key.name)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(valueType, namespace, name);
  }

  @Override
  public String toString() {
    return getNamespacedName();
  }
}

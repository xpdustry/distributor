package fr.xpdustry.distributor.metadata;

import cloud.commandframework.meta.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;
import java.util.function.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;

public final class MetadataContainer implements Buildable<MetadataContainer, MetadataContainer.Builder> {

  private static final MetadataContainer EMPTY = new MetadataContainer(Collections.emptyMap());

  private final Map<String, Supplier<?>> metas;

  private MetadataContainer(final Map<String, Supplier<?>> metas) {
    this.metas = metas;
  }

  public static MetadataContainer empty() {
    return EMPTY;
  }

  public static MetadataContainer.Builder builder() {
    return new MetadataContainer.Builder();
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> getMetadata(final String key) {
    return this.metas.containsKey(key) ? (Optional<T>) Optional.ofNullable(this.metas.get(key).get()) : Optional.empty();
  }

  public <T> Optional<T> getMetadata(final Key<T> key) {
    return getMetadata(key.getName());
  }

  public boolean hasMetadata(final String key) {
    return this.metas.containsKey(key);
  }

  public boolean hasMetadata(final Key<?> key) {
    return this.metas.containsKey(key.getName());
  }

  @UnmodifiableView
  public Map<String, ?> toMap() {
    final var map = new HashMap<String, Object>();
    this.metas.forEach((key, supplier) -> map.put(key, supplier.get()));
    return Collections.unmodifiableMap(map);
  }

  public CommandMeta toCommandMeta() {
    return new AsCommandMeta();
  }

  @Override
  public @NotNull Builder toBuilder() {
    return new Builder(this.metas);
  }

  public static final class Builder implements Buildable.Builder<MetadataContainer> {

    private final Map<String, Supplier<?>> metas = new HashMap<>();

    private Builder(final @NotNull Map<String, Supplier<?>> metas) {
      this.metas.putAll(metas);
    }

    private Builder() {
    }

    public <T> Builder withConstant(final @NotNull String key, final @NotNull T value) {
      this.metas.put(key, new ObjectSupplier<>(value));
      return this;
    }

    public <T> Builder withSupplier(final @NotNull String key, final @NotNull Supplier<T> supplier) {
      this.metas.put(key, supplier);
      return this;
    }

    public <T> Builder withConstant(final @NotNull Key<T> key, final @NotNull T value) {
      this.metas.put(key.getName(), new ObjectSupplier<>(value));
      return this;
    }

    public <T> Builder withSupplier(final @NotNull Key<T> key, final @NotNull Supplier<T> supplier) {
      this.metas.put(key.getName(), supplier);
      return this;
    }

    @Override
    public @NotNull MetadataContainer build() {
      return new MetadataContainer(Map.copyOf(metas));
    }
  }

  private static final class ObjectSupplier<T> implements Supplier<T> {

    private final T object;

    private ObjectSupplier(final T object) {
      this.object = object;
    }

    @Override
    public T get() {
      return object;
    }
  }

  private final class AsCommandMeta extends CommandMeta {

    @Override
    public @NonNull Optional<String> getValue(final @NonNull String key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull String getOrDefault(final @NonNull String key, final @NonNull String defaultValue) {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull <V> Optional<V> get(final CommandMeta.@NonNull Key<V> key) {
      var value = MetadataContainer.this.<V>getMetadata(key.getName());
      if (value.isEmpty() && key.getFallbackDerivation() != null) {
        value = Optional.ofNullable(key.getFallbackDerivation().apply(this));
      }
      return value;
    }

    @Override
    public <V> @NonNull V getOrDefault(final CommandMeta.@NonNull Key<V> key, final @NonNull V defaultValue) {
      return this.get(key).orElse(defaultValue);
    }

    @Override
    public @NonNull Map<@NonNull String, @NonNull String> getAll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Map<@NonNull String, @NonNull ?> getAllValues() {
      return MetadataContainer.this.toMap();
    }
  }
}

package fr.xpdustry.distributor.metadata;

import fr.xpdustry.distributor.util.*;
import io.leangen.geantyref.*;
import java.util.*;
import java.util.function.*;

public final class MetadataStore implements MetadataProvider, Buildable<MetadataStore, MetadataStore.Builder> {

  private final Map<String, Supplier<?>> metas;

  private MetadataStore(final Map<String, Supplier<?>> metas) {
    this.metas = metas;
  }

  public static MetadataStore.Builder builder() {
    return new MetadataStore.Builder();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> getMetadata(String key, TypeToken<T> type) {
    return metas.containsKey(key)
      ? (Optional<T>) Optional.ofNullable(metas.get(key).get())
      : Optional.empty();
  }

  @Override
  public Builder toBuilder() {
    final var builder = new Builder();
    builder.metas.putAll(metas);
    return builder;
  }

  public static final class Builder implements Buildable.Builder<MetadataStore> {

    private final Map<String, Supplier<?>> metas = new HashMap<>();

    private Builder() {
    }

    public <T> Builder withConstant(final String key, final T value) {
      this.metas.put(key, new ObjectSupplier<>(value));
      return this;
    }

    public <T> Builder withConstant(final Key<T> key, final T value) {
      this.metas.put(key.getName(), new ObjectSupplier<>(value));
      return this;
    }

    public <T> Builder withSupplier(final String key, final Supplier<T> supplier) {
      this.metas.put(key, supplier);
      return this;
    }

    public <T> Builder withSupplier(final Key<T> key, final Supplier<T> supplier) {
      this.metas.put(key.getName(), supplier);
      return this;
    }

    @Override
    public MetadataStore build() {
      return new MetadataStore(Map.copyOf(metas));
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
}

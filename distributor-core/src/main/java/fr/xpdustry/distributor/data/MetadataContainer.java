package fr.xpdustry.distributor.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class MetadataContainer implements MetadataProvider {

  private final Map<Key<?>, Supplier<?>> metas;

  public static MetadataContainer.Builder builder() {
    return new MetadataContainer.Builder();
  }

  private MetadataContainer(final Map<Key<?>, Supplier<?>> metas) {
    this.metas = metas;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> getMetadata(final Key<T> key) {
    return metas.containsKey(key)
      ? (Optional<T>) Optional.ofNullable(metas.get(key).get())
      : Optional.empty();
  }

  public static final class Builder {

    private final Map<Key<?>, Supplier<?>> metas = new HashMap<>();

    private Builder() {
    }

    public <T> Builder withConstant(final Key<T> key, final T value) {
      this.metas.put(key, new ObjectSupplier<>(value));
      return this;
    }

    public <T> Builder withSupplier(final Key<T> key, final Supplier<T> supplier) {
      this.metas.put(key, supplier);
      return this;
    }

    public MetadataContainer build() {
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
}

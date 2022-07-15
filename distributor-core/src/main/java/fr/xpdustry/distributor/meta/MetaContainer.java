package fr.xpdustry.distributor.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

// TODO Clean this...
public final class MetaContainer implements MetaProvider {

  @SuppressWarnings("rawtypes")
  private final Map<MetaKey, Supplier> metas;

  public static MetaContainer.Builder builder() {
    return new MetaContainer.Builder();
  }

  @SuppressWarnings("rawtypes")
  private MetaContainer(final Map<MetaKey, Supplier> metas) {
    this.metas = metas;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> getMeta(MetaKey<T> key) {
    return metas.containsKey(key)
      ? (Optional<T>) Optional.ofNullable(metas.get(key).get())
      : Optional.empty();
  }

  public static final class Builder {

    private final Map<MetaKey<?>, Supplier<?>> metas = new HashMap<>();

    private Builder() {
    }

    public <T> Builder withConstant(final MetaKey<T> key, final T value) {
      this.metas.put(key, new ObjectSupplier<>(value));
      return this;
    }

    public <T> Builder withSupplier(final MetaKey<T> key, final Supplier<T> supplier) {
      this.metas.put(key, supplier);
      return this;
    }

    public MetaContainer build() {
      return new MetaContainer(Map.copyOf(metas));
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

  // TODO Check effectiveness...
}

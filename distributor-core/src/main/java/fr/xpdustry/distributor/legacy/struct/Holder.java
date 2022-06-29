package fr.xpdustry.distributor.legacy.struct;

import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

/**
 * Holds a value and nothing else...
 *
 * @param <T> the element type
 */
public class Holder<T> {

  private @Nullable T value;

  public Holder() {
    this.value = null;
  }

  public Holder(final @Nullable T value) {
    this.value = value;
  }

  public static Holder<Boolean> getBool() {
    return new Holder<>(false);
  }

  public static Holder<Byte> getByte() {
    return new Holder<>((byte) 0);
  }

  public static Holder<Character> getChar() {
    return new Holder<>('\u0000');
  }

  public static Holder<Short> getShort() {
    return new Holder<>((short) 0);
  }

  public static Holder<Integer> getInt() {
    return new Holder<>(0);
  }

  public static Holder<Long> getLong() {
    return new Holder<>(0L);
  }

  public static Holder<Float> getFloat() {
    return new Holder<>(0F);
  }

  public static Holder<Double> getDouble() {
    return new Holder<>(0D);
  }

  public static Holder<String> getString() {
    return new Holder<>("");
  }

  public void set(final @Nullable T value) {
    this.value = value;
  }

  public @Nullable T get() {
    return value;
  }

  public Holder<T> use(final @NotNull Consumer<T> cons) {
    cons.accept(value);
    return this;
  }

  public <R> Holder<R> map(final @NotNull Function<T, R> func) {
    return new Holder<>(func.apply(value));
  }

  @SuppressWarnings("unchecked")
  public @NotNull <R> Holder<R> as() {
    return (Holder<R>) this;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public boolean equals(final @Nullable Object o) {
    return (o instanceof Holder<?> h) && Objects.equals(value, h.value);
  }

  @Override
  public @NotNull String toString() {
    return String.valueOf(value);
  }
}

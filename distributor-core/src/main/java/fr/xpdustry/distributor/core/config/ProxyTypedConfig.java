/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.core.config;

import java.util.*;
import java.util.function.*;
import mindustry.net.Administration.*;

public final class ProxyTypedConfig<T> extends Config implements TypedConfig<T> {

  private final Supplier<T> getter;
  private final Consumer<T> setter;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public ProxyTypedConfig(final String key, final String description, final T def, final Supplier<T> getter, final Consumer<T> setter) {
    super(key, description, def);
    this.getter = getter;
    this.setter = setter;
    getValueType();
  }

  @Override
  public boolean isNum() {
    return defaultValue instanceof Integer;
  }

  @Override
  public boolean isBool() {
    return defaultValue instanceof Boolean;
  }

  @Override
  public boolean isString() {
    return defaultValue instanceof String;
  }

  @Override
  public Object get() {
    return getter.get();
  }

  @Override
  public boolean bool() {
    return (boolean) getter.get();
  }

  @Override
  public int num() {
    return (int) getter.get();
  }

  @Override
  public String string() {
    return (String) getter.get();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void set(final Object value) {
    setter.accept(Objects.requireNonNull((T) value));
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public T getValue() {
    return getter.get();
  }

  @Override
  public void setValue(T value) {
    setter.accept(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T getDefaultValue() {
    return (T) defaultValue;
  }

  @Override
  public ValueType getValueType() {
    if (defaultValue instanceof String) {
      return ValueType.STRING;
    } else if (defaultValue instanceof Integer) {
      return ValueType.INTEGER;
    } else if (defaultValue instanceof Boolean) {
      return ValueType.BOOLEAN;
    } else {
      throw new IllegalStateException("The value contains an unknown value");
    }
  }

  @Override
  public Config getConfig() {
    return this;
  }
}

package fr.xpdustry.distributor.meta;

import fr.xpdustry.distributor.DistributorPlugin;
import java.util.Locale;
import java.util.Objects;
import mindustry.game.Team;

public final class MetaKey<V> {

  public static final MetaKey<String> NAME = MetaKey.of(String.class, DistributorPlugin.NAMESPACE, "name");
  public static final MetaKey<String> UUID = MetaKey.of(String.class, DistributorPlugin.NAMESPACE, "uuid");
  public static final MetaKey<Team> TEAM = MetaKey.of(Team.class, DistributorPlugin.NAMESPACE, "team");
  public static final MetaKey<Locale> LOCALE = MetaKey.of(Locale.class, DistributorPlugin.NAMESPACE, "locale");

  private final Class<V> type;
  private final String namespace;
  private final String name;

  public static <V> MetaKey<V> of(final Class<V> type, final String namespace, final String name) {
    return new MetaKey<>(type, namespace, name);
  }

  private MetaKey(final Class<V> type, final String namespace, final String name) {
    this.namespace = namespace;
    this.name = name;
    this.type = type;
  }

  public Class<V> getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof MetaKey<?> key) {
      return type.equals(key.type) && namespace.equals(key.namespace) && name.equals(key.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, namespace, name);
  }
}

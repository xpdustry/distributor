package fr.xpdustry.distributor.data;

import fr.xpdustry.distributor.DistributorPlugin;
import fr.xpdustry.distributor.data.*;
import java.util.Locale;
import mindustry.game.Team;

public final class StandardMetaKeys {

  public static final Key<String> NAME = Key.of(String.class, DistributorPlugin.NAMESPACE, "name");

  public static final Key<String> UUID = Key.of(String.class, DistributorPlugin.NAMESPACE, "uuid");

  public static final Key<Team> TEAM = Key.of(Team.class, DistributorPlugin.NAMESPACE, "team");

  public static final Key<Locale> LOCALE = Key.of(Locale.class, DistributorPlugin.NAMESPACE, "locale");

  public static final Key<Boolean> SERVER = Key.of(Boolean.class, DistributorPlugin.NAMESPACE, "server");

  private StandardMetaKeys() {
  }
}

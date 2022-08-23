package fr.xpdustry.distributor.metadata;

import fr.xpdustry.distributor.*;
import java.util.*;
import mindustry.game.*;

public final class StandardKeys {

  public static final Key<String> NAME = Key.of(String.class, DistributorPlugin.NAMESPACE, "name");

  public static final Key<String> DISPLAY_NAME = Key.of(String.class, DistributorPlugin.NAMESPACE, "display-name");

  public static final Key<String> UUID = Key.of(String.class, DistributorPlugin.NAMESPACE, "uuid");

  public static final Key<String> USID = Key.of(String.class, DistributorPlugin.NAMESPACE, "usid");

  public static final Key<Team> TEAM = Key.of(Team.class, DistributorPlugin.NAMESPACE, "team");

  public static final Key<Locale> LOCALE = Key.of(Locale.class, DistributorPlugin.NAMESPACE, "locale");

  private StandardKeys() {
  }
}

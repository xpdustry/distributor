package fr.xpdustry.distributor.data;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.struct.*;
import java.util.*;
import mindustry.game.*;

public final class StandardMetaKeys {

  public static final Key<String> NAME = Key.of(String.class, DistributorPlugin.NAMESPACE, "name");

  public static final Key<String> DISPLAY_NAME = Key.of(String.class, DistributorPlugin.NAMESPACE, "display-name");

  public static final Key<MUUID> MUUID = Key.of(MUUID.class, DistributorPlugin.NAMESPACE, "muuid");

  public static final Key<Team> TEAM = Key.of(Team.class, DistributorPlugin.NAMESPACE, "team");

  public static final Key<Locale> LOCALE = Key.of(Locale.class, DistributorPlugin.NAMESPACE, "locale");

  private StandardMetaKeys() {
  }
}

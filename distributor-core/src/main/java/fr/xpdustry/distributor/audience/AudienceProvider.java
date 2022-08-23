package fr.xpdustry.distributor.audience;

import mindustry.game.*;
import mindustry.gen.*;

public interface AudienceProvider {

  Audience all();

  Audience console();

  Audience player(final String uuid);

  Audience player(final Player player);

  Audience players();

  Audience team(final Team team);
}

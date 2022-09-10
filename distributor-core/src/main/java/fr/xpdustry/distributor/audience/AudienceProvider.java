package fr.xpdustry.distributor.audience;

import mindustry.gen.*;

public interface AudienceProvider {

  Audience all();

  Audience players();

  Audience world();

  Audience console();

  Audience player(final String uuid);

  Audience player(final Player player);
}

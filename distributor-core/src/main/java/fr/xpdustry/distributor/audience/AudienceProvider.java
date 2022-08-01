package fr.xpdustry.distributor.audience;

import mindustry.game.Team;

public interface AudienceProvider {

  Audience all();

  Audience console();

  Audience player(final String uuid);

  Audience players();

  Audience team(final Team team);
}

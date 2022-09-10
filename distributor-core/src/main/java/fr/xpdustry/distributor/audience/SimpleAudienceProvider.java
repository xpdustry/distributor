package fr.xpdustry.distributor.audience;

import arc.*;
import java.util.*;
import mindustry.game.*;
import mindustry.gen.*;

public class SimpleAudienceProvider implements AudienceProvider {

  private final Map<String, Audience> players = new HashMap<>();

  {
    Events.on(EventType.PlayerConnect.class, e -> players.put(e.player.uuid(), new PlayerAudience(e.player)));
    Events.on(EventType.PlayerLeave.class, e -> players.remove(e.player.uuid()));
  }

  @Override
  public Audience all() {
    return (ForwardingAudience) () -> List.of(console(), players());
  }

  @Override
  public Audience players() {
    return world();
  }

  @Override
  public Audience world() {
    return (ForwardingAudience) players::values;
  }

  @Override
  public final Audience console() {
    return ConsoleAudience.INSTANCE;
  }

  @Override
  public Audience player(final String uuid) {
    return players.getOrDefault(uuid, Audience.empty());
  }

  @Override
  public Audience player(final Player player) {
    return Objects.requireNonNull(players.get(player.uuid()), "player");
  }
}

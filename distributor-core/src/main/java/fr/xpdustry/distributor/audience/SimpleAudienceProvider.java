package fr.xpdustry.distributor.audience;

import arc.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;

public class SimpleAudienceProvider implements AudienceProvider {

  private final Map<String, Audience> players = new HashMap<>();

  {
    Events.on(EventType.PlayerConnect.class, e -> {
      players.put(e.player.uuid(), new PlayerAudience(e.player));
    });
    Events.on(EventType.PlayerLeave.class, e -> {
      players.remove(e.player.uuid());
    });
  }

  @Override
  public ForwardingAudience all() {
    return () -> List.of(console(), players());
  }

  @Override
  public Audience console() {
    return ConsoleAudience.INSTANCE;
  }

  @Override
  public Audience player(final String uuid) {
    return players.getOrDefault(uuid, Audience.empty());
  }

  @Override
  public ForwardingAudience players() {
    return players::values;
  }

  @Override
  public ForwardingAudience team(final Team team) {
    return () -> StreamSupport.stream(Groups.player.spliterator(), false)
      .filter(p -> p.team() == team)
      .map(Player::uuid)
      .map(this::player)
      .toList();
  }
}

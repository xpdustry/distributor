// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

import arc.Core;
import com.xpdustry.foundation.annotation.EventHandler;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.nucleus.dependency.Inject;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;

public final class MindustryMetricCollector implements PluginListener {

    private final LongAdder joinCounter = new LongAdder();
    private final LongAdder quitCounter = new LongAdder();
    private final LongAdder chatMessageCounter = new LongAdder();

    @Inject
    public MindustryMetricCollector() {}

    @EventHandler
    void onPlayerJoin(final EventType.PlayerJoin event) {
        this.joinCounter.increment();
    }

    @EventHandler
    void onPlayerLeave(final EventType.PlayerLeave event) {
        this.quitCounter.increment();
    }

    @EventHandler
    void onPlayerChatMessage(final EventType.PlayerChatEvent event) {
        this.chatMessageCounter.increment();
    }

    @MetricCollectorHandler
    void onMindustryMetricFlush(final MetricSink sink) {
        sink.sample("mindustry_player_joins_total", this.joinCounter.sum());
        sink.sample("mindustry_player_quits_total", this.quitCounter.sum());
        sink.sample("mindustry_chat_messages_total", this.chatMessageCounter.sum());

        sink.sample("mindustry_players_count", Groups.player.size());

        if (Vars.state != null) {
            sink.sample("mindustry_game_waves", Vars.state.wave);
            for (final var team : Team.all) {
                if (!team.active()) {
                    continue;
                }
                final var labels = Map.of("team", team.name);
                sink.sample("mindustry_buildings_count", team.data().buildings.size, labels);
                sink.sample("mindustry_units_count", team.data().units.size, labels);
                sink.sample("mindustry_team_players_count", team.data().players.size, labels);
            }
        }

        if (Core.graphics != null) {
            sink.sample("mindustry_server_tps", Core.graphics.getFramesPerSecond());
        }
    }
}

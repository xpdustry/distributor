package fr.xpdustry.distributor.audience;

import fr.xpdustry.distributor.struct.*;
import mindustry.game.*;
import mindustry.gen.*;

public interface AudienceProvider {

  Audience all();

  Audience console();

  Audience player(final MUUID muuid);

  Audience player(final Player player);

  Audience players();

  Audience team(final Team team);
}

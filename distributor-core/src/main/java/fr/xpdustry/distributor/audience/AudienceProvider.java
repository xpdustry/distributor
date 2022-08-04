package fr.xpdustry.distributor.audience;

import fr.xpdustry.distributor.struct.*;
import mindustry.game.*;

public interface AudienceProvider {

  Audience all();

  Audience console();

  Audience player(final MUUID muuid);

  Audience players();

  Audience team(final Team team);
}

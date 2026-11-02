// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.lifecycle;

import arc.Core;
import com.xpdustry.foundation.scheduler.MindustryThread;
import com.xpdustry.nucleus.dependency.Inject;
import mindustry.Vars;
import mindustry.net.Packets;

public final class RestartServiceImpl implements RestartService {

    @Inject
    public RestartServiceImpl() {}

    @Override
    public void restart() {
        MindustryThread.checkIsMainThread("restart");
        Vars.netServer.kickAll(Packets.KickReason.serverRestarting);
        Vars.net.dispose();
        Core.app.exit();
    }
}

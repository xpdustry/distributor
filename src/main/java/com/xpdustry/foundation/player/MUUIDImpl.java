// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.player;

import java.util.Objects;

record MUUIDImpl(String uuid, String usid) implements MUUID {
    MUUIDImpl {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(usid, "usid");
    }
}

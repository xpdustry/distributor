// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.player;

import java.util.Objects;

record MUUIDImpl(String uuid, String usid) implements MUUID {
    MUUIDImpl {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(usid, "usid");
    }
}

// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.message;

import org.jspecify.annotations.Nullable;

// TODO Unslop Later
record DiscoveryMessage(@Nullable ServerInfo info) implements Message {}

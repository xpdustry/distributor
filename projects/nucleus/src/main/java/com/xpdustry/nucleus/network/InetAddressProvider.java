// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.network;

import com.xpdustry.nucleus.dependency.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;

/// Provides the public address of this server.
public final class InetAddressProvider {

    private final InetAddress address;

    @Inject
    public InetAddressProvider() {
        // Our servers run within a pterodactyl container, which exposes the public address in SERVER_IP
        final var env = System.getenv("SERVER_IP");
        try {
            this.address = env == null ? InetAddress.getLocalHost() : InetAddress.getByName(env);
        } catch (final UnknownHostException e) {
            throw new IllegalStateException("Failed to resolve the address of this server", e);
        }
    }

    public InetAddress address() {
        return this.address;
    }
}

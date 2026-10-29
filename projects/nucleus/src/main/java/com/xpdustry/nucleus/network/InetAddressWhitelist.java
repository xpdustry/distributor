// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.network;

import com.xpdustry.nucleus.database.PostgresDatabase;
import com.xpdustry.nucleus.dependency.Inject;
import java.net.InetAddress;

public final class InetAddressWhitelist {

    private final PostgresDatabase database;

    @Inject
    public InetAddressWhitelist(final PostgresDatabase database) {
        this.database = database;
    }

    public boolean contains(final InetAddress address) {
        return this.database.withTransaction(handle -> handle.prepareStatement("""
                        SELECT 1 FROM "address_whitelist" "a"
                        WHERE ?::inet <<= "a"."address" LIMIT 1
                        """)
                .bind(address.getHostAddress())
                .executeSingleSelect(_ -> Boolean.TRUE)
                .orElse(false));
    }

    public void insert(final InetAddress address, final String reason) {
        this.database.withTransaction(handle -> handle.prepareStatement("""
                        INSERT INTO "address_whitelist"("address", "reason") VALUES (?::inet, ?)
                        """)
                .bind(address.getHostAddress())
                .bind(reason)
                .executeSingleUpdate());
    }
}

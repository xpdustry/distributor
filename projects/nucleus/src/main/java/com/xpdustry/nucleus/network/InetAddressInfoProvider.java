// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.network;

import com.google.gson.Gson;
import com.xpdustry.foundation.annotation.ScheduledTaskHandler;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryTimeUnit;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.config.ConfigPropertyKey;
import com.xpdustry.nucleus.database.PostgresDatabase;
import com.xpdustry.nucleus.dependency.Inject;
import com.xpdustry.nucleus.http.URIBuilder;
import java.net.InetAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public final class InetAddressInfoProvider implements PluginListener {

    private final PluginLogger logger;

    private final ConfigManager config;
    private final Gson gson;
    private final HttpClient http;
    private final PostgresDatabase database;

    @Inject
    public InetAddressInfoProvider(
            final ConfigManager config,
            final Gson gson,
            final HttpClient http,
            final PostgresDatabase database,
            final PluginLogger logger) {
        this.logger = logger;
        this.config = config;
        this.gson = gson;
        this.http = http;
        this.database = database;
    }

    public Optional<InetAddressInfo> get(final InetAddress address) {
        final var cached = this.database.withTransaction(handle -> handle.prepareStatement("""
                        SELECT a."safe", a."country_code", a."asn_name", a."asn_number"
                        FROM "address_info_request_cache" a
                        WHERE a."address" = ?::inet AND a."updated_at" + a."ttl" >= current_timestamp
                        """)
                .bind(address.getHostAddress())
                .executeSingleSelect(result -> new InetAddressInfo(
                        result.getBoolean("safe"),
                        result.getString("country_code"),
                        result.getString("asn_name"),
                        result.getLong("asn_number"))));
        if (cached.isPresent()) {
            return cached;
        }

        final var token = this.config.get(ConfigPropertyKey.VPN_API_IO_TOKEN);
        if (token.isEmpty()) {
            return Optional.empty();
        }

        final HttpResponse<String> response;
        try {
            response = this.http.send(
                    HttpRequest.newBuilder(new URIBuilder("https://vpnapi.io/api/")
                                    .addPathSegment(address.getHostAddress())
                                    .addParameter("key", token)
                                    .build())
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
        } catch (final Exception e) {
            this.logger.error(
                    "An error occurred while retrieving info about {} from vpnapi.io", address.getHostAddress(), e);
            return Optional.empty();
        }

        final var code = response.statusCode();
        if (code != 200) {
            this.logger.error(
                    "An unexpected response has been received from vpnapi.io while retrieving info about {} (status_code={}, body={})",
                    address.getHostAddress(),
                    code,
                    response.body());
            return Optional.empty();
        }

        try {
            final var body = this.gson.fromJson(response.body(), VpnApiIoResponse.class);
            final var safe = !(body.security.vpn || body.security.proxy || body.security.tor || body.security.relay);
            final var countryCode = body.location.country_code;
            final var asnName = body.network.autonomous_system_organization;
            final var asnNumber = Long.parseLong(body.network.autonomous_system_number.replaceFirst("AS", ""));

            this.database.withTransaction(handle -> handle.prepareStatement("""
                        INSERT INTO "address_info_request_cache"("address", "safe", "country_code", "asn_name", "asn_number")
                        VALUES (?::inet, ?, ?, ?, ?)
                        ON CONFLICT ("address") DO UPDATE SET
                            "updated_at"    = current_timestamp,
                            "safe"          = excluded."safe",
                            "country_code"  = excluded."country_code",
                            "asn_name"      = excluded."asn_name",
                            "asn_number"    = excluded."asn_number"
                        """)
                    .bind(address.getHostAddress())
                    .bind(safe)
                    .bind(countryCode)
                    .bind(asnName)
                    .bind(asnNumber)
                    .executeSingleUpdate());

            return Optional.of(new InetAddressInfo(safe, countryCode, asnName, asnNumber));
        } catch (final Exception e) {
            this.logger.error(
                    "Failed to deserialize the response from vpnapi.io while retrieving info about {} (json={})",
                    address.getHostAddress(),
                    response.body(),
                    e);
            return Optional.empty();
        }
    }

    @ScheduledTaskHandler(initialDelay = 0, delay = 12, unit = MindustryTimeUnit.HOURS)
    void onHousekeeping() {
        try {
            this.database.withTransaction(handle -> handle.prepareStatement("""
                    DELETE FROM "address_info_request_cache" a
                    WHERE a."updated_at" + a."ttl" < current_timestamp
                    """).executeUpdate());
        } catch (final Exception e) {
            this.logger.error("Failed to run housekeeping workflow", e);
        }
    }

    // https://vpnapi.io/api-documentation
    private record VpnApiIoResponse(Security security, Location location, Network network) {

        record Security(boolean vpn, boolean proxy, boolean tor, boolean relay) {}

        record Location(String country_code) {}

        record Network(String autonomous_system_number, String autonomous_system_organization) {}
    }
}

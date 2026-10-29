// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.http;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class URIBuilder {
    private final List<String> path = new ArrayList<>();
    private final Map<String, String> parameters = new HashMap<>();
    private final URI base;

    public URIBuilder(final String base) {
        this.base = URI.create(base);
    }

    public URIBuilder(final URI base) {
        this.base = base;
    }

    public URIBuilder addPathSegment(final String segment) {
        this.path.add(escape(segment));
        return this;
    }

    public URIBuilder addParameter(final String parameter, final String value) {
        this.parameters.put(escape(parameter), escape(value));
        return this;
    }

    public URI build() {
        final var base = this.base.toString();
        final var builder = new StringBuilder(base.length() * 2).append(base);
        for (var i = 0; i < this.path.size(); i++) {
            if (i > 0 || !base.endsWith("/")) {
                builder.append('/');
            }
            builder.append(this.path.get(i));
        }
        var first = true;
        for (final var parameter : this.parameters.entrySet()) {
            builder.append(first ? '?' : '&');
            first = false;
            builder.append(parameter.getKey()).append('=').append(parameter.getValue());
        }
        return URI.create(builder.toString());
    }

    private static String escape(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

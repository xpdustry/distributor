/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.logger.simple;

import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public final class DistributorLoggerProvider implements SLF4JServiceProvider {

    private @Nullable ILoggerFactory loggerFactory = null;
    private @Nullable IMarkerFactory markerFactory = null;
    private @Nullable MDCAdapter mdcAdapter = null;

    @Override
    public ILoggerFactory getLoggerFactory() {
        return Objects.requireNonNull(this.loggerFactory);
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return Objects.requireNonNull(this.markerFactory);
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return Objects.requireNonNull(this.mdcAdapter);
    }

    @Override
    public String getRequestedApiVersion() {
        return "2.0.0";
    }

    @Override
    public void initialize() {
        this.loggerFactory = new DistributorLoggerFactory();
        this.markerFactory = new BasicMarkerFactory();
        this.mdcAdapter = new NOPMDCAdapter();
    }
}

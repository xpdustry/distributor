/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.core.logging;

import org.checkerframework.checker.nullness.qual.*;
import org.slf4j.*;
import org.slf4j.helpers.*;
import org.slf4j.spi.*;

public final class ArcServiceProvider implements SLF4JServiceProvider {

  @MonotonicNonNull
  private ILoggerFactory loggerFactory = null;

  @MonotonicNonNull
  private IMarkerFactory markerFactory = null;

  @MonotonicNonNull
  private MDCAdapter mdcAdapter = null;

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return markerFactory;
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return mdcAdapter;
  }

  @Override
  public String getRequestedApiVersion() {
    return "2.0.0";
  }

  @Override
  public void initialize() {
    loggerFactory = new ArcLoggerFactory();
    markerFactory = new BasicMarkerFactory();
    mdcAdapter = new NOPMDCAdapter();
  }
}

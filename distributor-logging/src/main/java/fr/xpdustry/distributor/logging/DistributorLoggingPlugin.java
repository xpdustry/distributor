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
package fr.xpdustry.distributor.logging;

import arc.util.*;
import fr.xpdustry.distributor.plugin.*;
import org.slf4j.*;

public final class DistributorLoggingPlugin extends ExtendedPlugin {

  {
    // Class loader trickery to use the ModClassLoader instead of the root
    final var temp = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    LoggerFactory.getILoggerFactory();
    Thread.currentThread().setContextClassLoader(temp);
  }

  @Override
  public void onInit() {
    if (getLogger() instanceof ArcLogger) {
      getLogger().info("Successfully loaded Distributor logger.");
    } else {
      Log.warn("Failed to load Distributor logger.");
    }
  }
}

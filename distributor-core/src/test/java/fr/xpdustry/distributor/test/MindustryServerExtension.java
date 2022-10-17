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
package fr.xpdustry.distributor.test;

import arc.*;
import java.util.concurrent.*;
import mindustry.server.*;
import org.junit.jupiter.api.extension.*;

// TODO Testing this thing
public final class MindustryServerExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

  @Override
  public void beforeAll(final ExtensionContext context) {
    if (Core.app == null) {
      System.out.println("Starting Mindustry server");
      ServerLauncher.main(new String[] {});
      final var future = new CompletableFuture<Void>();
      Core.app.addListener(new ApplicationListener() {
        @Override
        public void init() {
          future.complete(null);
        }
      });
      future.orTimeout(10L, TimeUnit.SECONDS).join();
    }
  }

  @Override
  public void close() {
    Core.app.exit();
  }
}

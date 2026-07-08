// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import org.slf4j.Logger;

record PluginFacadeImpl(Logger logger, PluginMetadata metadata) implements PluginFacade {}

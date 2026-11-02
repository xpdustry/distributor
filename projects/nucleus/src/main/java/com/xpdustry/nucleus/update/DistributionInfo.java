// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.nucleus.version.MindustryVersion;
import java.nio.file.Path;
import java.util.Map;

/// The distribution installed on this server, the files an update may replace.
///
/// @param server the jar of the Mindustry server
/// @param mods the directory of the plugins, where the missing dependencies are installed
/// @param plugins the jars of the installed monorepo plugins, by name
public record DistributionInfo(Path server, MindustryVersion mindustry, Path mods, Map<String, Path> plugins) {}

// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

/// Published on the main thread when the [UpdateService] stages an update, applied on the next restart.
///
/// @param distribution the staged distribution, such as `2026.10.1 (b1e2c3d4)`
public record UpdateStagedEvent(String distribution) {}

// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import java.io.IOException;
import java.io.Serial;

/// Thrown when the latest distribution needs more than this server has, which only its owner can fix.
final class IncompatibleUpdateException extends IOException {

    @Serial
    private static final long serialVersionUID = 1L;

    IncompatibleUpdateException(final String message) {
        super(message);
    }
}

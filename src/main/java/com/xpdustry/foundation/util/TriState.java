// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.util;

import org.jspecify.annotations.Nullable;

/// A ternary boolean type.
///
/// This is equivalent to `@Nullable Boolean`, but safer.
public enum TriState {
    /// The defined `false` state.
    FALSE(false),
    /// The defined `true` state.
    TRUE(true),
    /// The undefined state.
    UNDEFINED(false);

    /// Returns [TRUE] or [FALSE] for the given boolean.
    ///
    /// @param state the boolean value
    /// @return [TRUE] when the state is `true`, otherwise [FALSE]
    public static TriState of(final boolean state) {
        return state ? TRUE : FALSE;
    }

    /// Returns a tri-state value for a nullable boolean.
    ///
    /// @param state the nullable boolean value
    /// @return [UNDEFINED] when the state is `null`, otherwise [TRUE] or [FALSE]
    public static TriState of(final @Nullable Boolean state) {
        return state == null ? UNDEFINED : state ? TRUE : FALSE;
    }

    private final boolean value;

    TriState(final boolean value) {
        this.value = value;
    }

    /// Returns this state as a boolean.
    ///
    /// [UNDEFINED] returns `false`.
    ///
    /// @return the boolean value of this state
    public boolean asBoolean() {
        return this.value;
    }
}

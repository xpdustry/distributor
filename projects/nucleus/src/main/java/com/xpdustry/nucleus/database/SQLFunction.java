// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.database;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLFunction<I, O> {
    O apply(I input) throws SQLException;
}

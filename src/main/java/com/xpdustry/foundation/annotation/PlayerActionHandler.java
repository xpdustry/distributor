// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks a method as a player action handler.
///
/// The annotated method is called by [mindustry.net.Administration#allowAction].
/// It must have a single [mindustry.net.Administration.PlayerAction] parameter
/// and return a `boolean` indicating whether the action is allowed.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PlayerActionHandler {}

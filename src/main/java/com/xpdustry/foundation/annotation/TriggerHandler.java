// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.util.Priority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import mindustry.game.EventType;

/// Marks a method as a trigger handler.
///
/// The annotated method is called by [com.xpdustry.foundation.event.EventPublisher]
/// when its corresponding trigger is posted. It must have no parameters.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TriggerHandler {

    /// The trigger to listen for.
    EventType.Trigger value();

    /// The priority of the trigger handler.
    Priority priority() default Priority.NORMAL;
}

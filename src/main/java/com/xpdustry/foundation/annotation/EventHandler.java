// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.annotation;

import com.xpdustry.foundation.util.Priority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks a method as an event handler.
///
/// The annotated method is called by [com.xpdustry.foundation.event.EventPublisher]
/// when its corresponding event is posted. It must have exactly one parameter, which is the event type.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /// The priority of the event handler.
    Priority priority() default Priority.NORMAL;
}

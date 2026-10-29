// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.annotation;

import com.google.errorprone.annotations.Keep;
import com.xpdustry.foundation.util.Priority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Marks a method as an event handler.
///
/// The annotated method is called by [com.xpdustry.foundation.event.EventPublisher]
/// when its corresponding event is posted. It must have exactly one parameter, which is the event type.
@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /// The priority of the event handler.
    Priority priority() default Priority.NORMAL;
}

// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

import com.google.errorprone.annotations.Keep;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Keep
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MetricCollectorHandler {}

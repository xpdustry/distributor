// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.pipeline;

@FunctionalInterface
public interface Processor<I, O> {

    O process(I context);
}

// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.pipeline;

import com.xpdustry.foundation.util.Priority;

public interface ProcessorPipeline<I, O> {

    void register(String name, Priority priority, Processor<I, O> processor);

    O pump(I context);
}

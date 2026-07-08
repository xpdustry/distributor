// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.collection;

import arc.struct.ObjectSet;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public final class MindustrySetTest extends TestCase {

    public static TestSuite suite() {
        final var suite = new TestSuite();
        suite.addTest(SetTestSuiteBuilder.using(new TestArcSetGenerator())
                .named(MindustrySet.class.getSimpleName())
                .withFeatures(List.of(SetFeature.values()))
                .withFeatures(List.of(CollectionSize.ANY))
                .createTestSuite());
        return suite;
    }

    private static final class TestArcSetGenerator extends TestStringSetGenerator {

        @Override
        protected Set<String> create(final String[] elements) {
            final var set = new MindustrySet<String>(new ObjectSet<>());
            set.addAll(Arrays.asList(elements));
            return set;
        }
    }
}

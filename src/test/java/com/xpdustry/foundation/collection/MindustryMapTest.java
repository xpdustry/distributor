// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.collection;

import arc.struct.ObjectMap;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public final class MindustryMapTest extends TestCase {

    public static TestSuite suite() {
        final var suite = new TestSuite();
        suite.addTest(MapTestSuiteBuilder.using(new TestArcMapGenerator())
                .named(MindustryMap.class.getSimpleName())
                .withFeatures(
                        List.of(MapFeature.GENERAL_PURPOSE, MapFeature.ALLOWS_NULL_VALUES, MapFeature.SUPPORTS_REMOVE))
                .withFeatures(List.of(CollectionSize.ANY, CollectionFeature.SUPPORTS_ITERATOR_REMOVE))
                .createTestSuite());
        return suite;
    }

    private static final class TestArcMapGenerator extends TestStringMapGenerator {

        @Override
        protected Map<String, String> create(final Map.Entry<String, String>[] entries) {
            final var map = new MindustryMap<String, String>(new ObjectMap<>());
            for (final var entry : entries) map.put(entry.getKey(), entry.getValue());
            return map;
        }
    }
}

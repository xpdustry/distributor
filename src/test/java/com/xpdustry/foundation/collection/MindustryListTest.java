// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.collection;

import arc.struct.Seq;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public final class MindustryListTest extends TestCase {

    public static TestSuite suite() {
        final var suite = new TestSuite();
        suite.addTest(ListTestSuiteBuilder.using(new TestMindustryListGenerator())
                .named(MindustryList.class.getSimpleName())
                .withFeatures(List.of(ListFeature.values()))
                .withFeatures(List.of(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUBSET_VIEW,
                        CollectionFeature.DESCENDING_VIEW))
                .createTestSuite());
        return suite;
    }

    private static final class TestMindustryListGenerator extends TestStringListGenerator {

        @Override
        protected List<String> create(final String[] elements) {
            final var list = new MindustryList<String>(new Seq<>(String.class));
            list.addAll(Arrays.asList(elements));
            return list;
        }
    }
}

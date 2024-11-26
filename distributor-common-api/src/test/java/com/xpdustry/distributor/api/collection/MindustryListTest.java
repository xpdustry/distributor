/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.api.collection;

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

/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.util;

import arc.struct.Seq;
import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ArcListTest {

    @Test
    void testSuite() {
        final var suite = new TestSuite();
        suite.addTest(ListTestSuiteBuilder.using(new TestArcListGenerator())
                .named("ArcList")
                .withFeatures(List.of(ListFeature.values()))
                .withFeatures(List.of(
                        CollectionSize.ANY,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.SUBSET_VIEW,
                        CollectionFeature.DESCENDING_VIEW))
                .createTestSuite());
        final var result = new TestResult();
        suite.run(result);
        if (!result.wasSuccessful()) {
            @SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder") // tf ?
            final var builder = new StringBuilder();
            builder.append("ArcList Test suite failed:\n");
            result.failures().asIterator().forEachRemaining(failure -> builder.append(failure.toString())
                    .append('\n'));
            result.errors().asIterator().forEachRemaining(error -> builder.append(error.toString())
                    .append('\n'));
            Assertions.fail(builder.toString());
        }
    }

    private static final class TestArcListGenerator extends TestStringListGenerator {

        @Override
        protected List<String> create(String[] elements) {
            final var list = new ArcList<String>(new Seq<>(String.class));
            list.addAll(Arrays.asList(elements));
            return list;
        }
    }
}

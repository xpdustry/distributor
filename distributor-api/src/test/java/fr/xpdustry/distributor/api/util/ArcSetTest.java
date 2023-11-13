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

import arc.struct.ObjectSet;
import com.google.common.collect.testing.SetTestSuiteBuilder;
import com.google.common.collect.testing.TestStringSetGenerator;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.SetFeature;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ArcSetTest {

    @Test
    void testSuite() {
        final var suite = new TestSuite();
        suite.addTest(SetTestSuiteBuilder.using(new TestArcSetGenerator())
                .named("ArcSet")
                .withFeatures(List.of(SetFeature.values()))
                .withFeatures(List.of(CollectionSize.ANY))
                .createTestSuite());
        final var result = new TestResult();
        suite.run(result);
        if (!result.wasSuccessful()) {
            @SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder") // tf ?
            final var builder = new StringBuilder();
            builder.append("ArcSet TEst suite failed:\n");
            result.failures().asIterator().forEachRemaining(failure -> builder.append(failure.toString())
                    .append('\n'));
            result.errors().asIterator().forEachRemaining(error -> builder.append(error.toString())
                    .append('\n'));
            Assertions.fail(builder.toString());
        }
    }

    private static final class TestArcSetGenerator extends TestStringSetGenerator {

        @Override
        protected Set<String> create(String[] elements) {
            final var set = new ArcSet<String>(new ObjectSet<>());
            set.addAll(Arrays.asList(elements));
            return set;
        }
    }
}

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

import arc.struct.ObjectMap;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;
import java.util.List;
import java.util.Map;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class ArcMapTest {

    @Test
    void testSuite() {
        final var suite = new TestSuite();
        suite.addTest(MapTestSuiteBuilder.using(new TestArcMapGenerator())
                .named("ArcMap")
                .withFeatures(
                        List.of(MapFeature.GENERAL_PURPOSE, MapFeature.ALLOWS_NULL_VALUES, MapFeature.SUPPORTS_REMOVE))
                .withFeatures(List.of(
                        CollectionSize.ANY,
                        CollectionFeature.NON_STANDARD_TOSTRING,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE))
                .createTestSuite());
        final var result = new TestResult();
        suite.run(result);
        if (!result.wasSuccessful()) {
            @SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder") // tf ?
            final var builder = new StringBuilder();
            builder.append("ArcMap Test suite failed:\n");
            result.failures().asIterator().forEachRemaining(failure -> builder.append(failure.toString())
                    .append('\n'));
            result.errors().asIterator().forEachRemaining(error -> builder.append(error.toString())
                    .append('\n'));
            Assertions.fail(builder.toString());
        }
    }

    private static final class TestArcMapGenerator extends TestStringMapGenerator {

        @Override
        protected Map<String, String> create(final Map.Entry<String, String>[] entries) {
            final var map = new ArcMap<String, String>(new ObjectMap<>());
            for (final var entry : entries) map.put(entry.getKey(), entry.getValue());
            return map;
        }
    }
}

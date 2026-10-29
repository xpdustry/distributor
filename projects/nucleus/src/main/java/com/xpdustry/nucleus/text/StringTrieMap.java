// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public final class StringTrieMap<V> {

    private @Nullable Map<Character, StringTrieMap<V>> children = null;
    private @Nullable V value = null;

    public record Token<V>(String word, int index, V value) {}

    public List<Token<V>> search(final CharSequence chars) {
        final List<Token<V>> tokens = new ArrayList<>();
        final List<Accumulator<V>> accumulators = new ArrayList<>();

        for (int i = 0; i < chars.length(); i++) {
            final var c = chars.charAt(i);

            if (this.children != null && this.children.containsKey(c)) {
                accumulators.add(new Accumulator<>(this, i));
            }

            for (int j = 0; j < accumulators.size(); j++) {
                final var accumulator = accumulators.get(j);

                final var child = accumulator.node.children == null ? null : accumulator.node.children.get(c);
                if (child == null) {
                    accumulators.remove(j);
                    j--;
                    continue;
                } else {
                    accumulator.node = child;
                }

                final var value = accumulator.node.value;
                if (value != null) {
                    tokens.add(new Token<>(
                            chars.subSequence(accumulator.index, i + 1).toString(), accumulator.index, value));
                }
            }
        }

        return tokens;
    }

    public @Nullable V get(final CharSequence chars) {
        var node = this;
        for (int i = 0; i < chars.length(); i++) {
            final var c = chars.charAt(i);
            if (node.children == null) {
                return null;
            }
            node = node.children.get(c);
            if (node == null) {
                return null;
            }
        }

        return node.value;
    }

    public boolean contains(final CharSequence chars, final boolean partial) {
        var node = this;
        for (int i = 0; i < chars.length(); i++) {
            final var c = chars.charAt(i);
            if (node.children == null) {
                return false;
            }
            node = node.children.get(c);
            if (node == null) {
                return false;
            }
        }

        return node.value != null || partial;
    }

    public @Nullable V put(final CharSequence chars, final V value) {
        StringTrieMap<V> node = this;

        for (int i = 0; i < chars.length(); i++) {
            final var c = chars.charAt(i);
            if (node.children == null) {
                node.children = new HashMap<>();
            }
            node = node.children.computeIfAbsent(c, _ -> new StringTrieMap<>());
        }

        final var previous = node.value;
        node.value = value;
        return previous;
    }

    private static final class Accumulator<V> {
        private StringTrieMap<V> node;
        private final int index;

        private Accumulator(final StringTrieMap<V> node, final int index) {
            this.node = node;
            this.index = index;
        }
    }
}

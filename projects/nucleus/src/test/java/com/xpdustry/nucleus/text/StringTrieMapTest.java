// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.text;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class StringTrieMapTest {

    @Test
    void put_and_get() {
        final var trie = new StringTrieMap<Integer>();
        trie.put("test", 0);
        trie.put("test1", 1);
        trie.put("test2", 2);

        assertThat(trie.get("test")).isEqualTo(0);
        assertThat(trie.get("test1")).isEqualTo(1);
        assertThat(trie.get("test2")).isEqualTo(2);
        assertThat(trie.get("test3")).isNull();
    }

    @Test
    void contains() {
        final var trie = new StringTrieMap<Integer>();
        trie.put("test", 0);

        assertThat(trie.contains("test", false)).isTrue();
        assertThat(trie.contains("test", true)).isTrue();
        assertThat(trie.contains("te", false)).isFalse();
        assertThat(trie.contains("te", true)).isTrue();
        assertThat(trie.contains("tex", false)).isFalse();
        assertThat(trie.contains("tex", true)).isFalse();
    }

    @Test
    void search() {
        final var trie = new StringTrieMap<Integer>();
        trie.put("dang", 0);
        trie.put("cat", 1);
        trie.put("catch", 2);
        final var result = trie.search("dang, this cat is hard to catch indeed");

        assertThat(result)
                .containsExactly(
                        new StringTrieMap.Token<>("dang", 0, 0),
                        new StringTrieMap.Token<>("cat", 11, 1),
                        new StringTrieMap.Token<>("cat", 26, 1),
                        new StringTrieMap.Token<>("catch", 26, 2));
    }

    @Test
    void search_unicode() {
        final var trie = new StringTrieMap<Integer>();
        trie.put("😏", 0);
        trie.put("привет", 1);
        final var result = trie.search("test 😏 привет");

        assertThat(result)
                .containsExactly(new StringTrieMap.Token<>("😏", 5, 0), new StringTrieMap.Token<>("привет", 8, 1));
    }
}

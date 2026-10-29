// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.text;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.nucleus.dependency.Inject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class BadWordFinder implements PluginListener {

    private final StringTrieMap<BadWordCategory> trie = new StringTrieMap<>();
    private final Gson gson;

    @Inject
    public BadWordFinder(final Gson gson) {
        this.gson = gson;
    }

    public List<String> findBadWords(final String text, final Set<BadWordCategory> categories) {
        final var lowercase = text.toLowerCase(Locale.ROOT);
        return this.trie.search(lowercase).stream()
                .filter(token -> categories.contains(token.value())
                        && (token.word().length() >= 5 || isSurroundedBySpaceChars(lowercase, token)))
                .map(StringTrieMap.Token::word)
                .toList();
    }

    @Override
    public void onInit() {
        final var stream =
                this.getClass().getClassLoader().getResourceAsStream("com/xpdustry/nucleus/text/bad_words.json");
        Objects.requireNonNull(stream, "bad_words.json");
        try (stream;
                final var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            final var map = this.gson.fromJson(reader, new TypeToken<Map<BadWordCategory, List<String>>>() {});
            for (final var entry : map.entrySet()) {
                for (final var word : entry.getValue()) {
                    this.trie.put(word, entry.getKey());
                }
            }
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to load the bad_words.json file", e);
        }
    }

    private static boolean isSurroundedBySpaceChars(final String text, final StringTrieMap.Token<?> token) {
        if (token.index() == 0 || Character.isSpaceChar(text.charAt(token.index() - 1))) {
            final var end = token.index() + token.word().length();
            return end >= text.length() || Character.isSpaceChar(text.charAt(end));
        }
        return false;
    }
}

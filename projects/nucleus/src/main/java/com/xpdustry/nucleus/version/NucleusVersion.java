// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

import java.util.Comparator;

/// A calendar-based version for nucleus, a snapshot being a build of the next release.
public record NucleusVersion(int year, int month, int build, boolean snapshot) implements Comparable<NucleusVersion> {

    private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";

    private static final Comparator<NucleusVersion> COMPARATOR = Comparator.comparingInt(NucleusVersion::year)
            .thenComparingInt(NucleusVersion::month)
            .thenComparingInt(NucleusVersion::build)
            .thenComparing(NucleusVersion::snapshot, Comparator.reverseOrder());

    public NucleusVersion {
        if (year < 0) {
            throw new IllegalArgumentException("Year must be positive");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (build < 0) {
            throw new IllegalArgumentException("Build must be positive");
        }
    }

    public NucleusVersion(final String version) {
        var normalized = version;
        if (normalized.startsWith("v")) {
            normalized = normalized.substring(1);
        }
        final var snapshot = normalized.endsWith(SNAPSHOT_SUFFIX);
        if (snapshot) {
            normalized = normalized.substring(0, normalized.length() - SNAPSHOT_SUFFIX.length());
        }

        final var parts = normalized.split("\\.", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Version must be in format 'year.month.build[-SNAPSHOT]', got " + version);
        }
        final int year, month, build;
        try {
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            build = Integer.parseInt(parts[2]);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Version must be in format 'year.month.build[-SNAPSHOT]', got " + version, e);
        }

        this(year, month, build, snapshot);
    }

    @Override
    public int compareTo(final NucleusVersion other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return this.year + "." + this.month + "." + this.build + (this.snapshot ? SNAPSHOT_SUFFIX : "");
    }
}

// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.version;

/// The calendar version of nucleus, as YEAR.MONTH.BUILD.
public record NucleusVersion(int year, int month, int build) implements Comparable<NucleusVersion> {

    public static NucleusVersion parse(final String version) {
        final var parts = version.split("\\.", -1);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Expected YEAR.MONTH.BUILD, got " + version);
        }
        return new NucleusVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    @Override
    public int compareTo(final NucleusVersion other) {
        if (this.year != other.year) {
            return Integer.compare(this.year, other.year);
        }
        if (this.month != other.month) {
            return Integer.compare(this.month, other.month);
        }
        return Integer.compare(this.build, other.build);
    }

    @Override
    public String toString() {
        return this.year + "." + this.month + "." + this.build;
    }
}

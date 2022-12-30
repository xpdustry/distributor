/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.core.dependency;

import java.util.Base64;

// Simpler Library class from https://github.com/Byteflux/libby, under the MIT license.
public record Dependency(String group, String artifact, String version, byte[] checksum) {
    public Dependency(final String group, final String artifact, final String version, final byte[] checksum) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
        this.checksum = checksum.clone();
    }

    public Dependency(final String group, final String artifact, final String version, final String checksum) {
        this(group, artifact, version, Base64.getDecoder().decode(checksum));
    }

    public Dependency(final String group, final String artifact, final String version) {
        this(group, artifact, version, new byte[0]);
    }

    public String path() {
        return this.group.replace('.', '/')
                + '/' + this.artifact
                + '/' + this.version
                + '/' + this.artifact
                + '-' + this.version + ".jar";
    }

    @Override
    public byte[] checksum() {
        return this.checksum.clone();
    }
}

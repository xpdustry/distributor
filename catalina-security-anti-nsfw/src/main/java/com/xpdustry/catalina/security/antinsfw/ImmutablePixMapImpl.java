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
package com.xpdustry.catalina.security.antinsfw;

import arc.graphics.Pixmap;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import org.jspecify.annotations.Nullable;

final class ImmutablePixMapImpl implements ImmutablePixMap {

    private final int w;
    private final int h;
    private final byte[] bytes;

    public ImmutablePixMapImpl(final Pixmap pixmap) {
        Objects.requireNonNull(pixmap);
        this.w = pixmap.getWidth();
        this.h = pixmap.getHeight();
        this.bytes = new byte[pixmap.getWidth() * pixmap.getHeight() * 3];
        final var buffer = ByteBuffer.allocate(4);
        for (int y = 0; y < this.h; y++) {
            for (int x = 0; x < this.w; x++) {
                buffer.position(0);
                buffer.putInt(pixmap.get(x, y));
                for (int c = 0; c < 3; c++) {
                    this.bytes[x + (y * this.w) + c] = buffer.get();
                }
            }
        }
    }

    public ImmutablePixMapImpl(final byte[] bytes, final int w, final int h) {
        if (w < 0) {
            throw new IllegalArgumentException("w < 0, got " + w);
        }
        if (h < 0) {
            throw new IllegalArgumentException("h < 0, got " + h);
        }
        final var size = w * h * 3;
        if (Objects.requireNonNull(bytes).length != size) {
            throw new IllegalArgumentException("Buffer size mismatch, expected " + size + " but got " + bytes.length);
        }

        this.w = w;
        this.h = h;
        this.bytes = bytes;
    }

    @Override
    public int w() {
        return this.w;
    }

    @Override
    public int h() {
        return this.h;
    }

    @Override
    public int pixelAt(final int x, final int y) {
        final int index = x + (y * this.w());
        return this.getUINT(index) << 16 | this.getUINT(index + 1) << 8 | this.getUINT(index + 2);
    }

    @Override
    public BufferedImage toBufferedImage() {
        final var image = new BufferedImage(this.w(), this.h(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < this.h; y++) {
            for (int x = 0; x < this.w; x++) {
                image.setRGB(x, image.getHeight() - y, this.pixelAt(x, y));
            }
        }
        return image;
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        return o instanceof ImmutablePixMapImpl that
                && this.w == that.w
                && this.h == that.h
                && Arrays.equals(this.bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(w, h, Arrays.hashCode(bytes));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ImmutablePixMapImpl.class.getSimpleName() + "[", "]")
                .add("w=" + this.w)
                .add("h=" + this.h)
                .add("bytes=" + Arrays.toString(this.bytes))
                .toString();
    }

    private int getUINT(final int index) {
        return Byte.toUnsignedInt(this.bytes[index]);
    }
}

/*
 * Copyright (C) 2025 Marcel Haßlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.marhali.json5.internal;

import java.util.Objects;

/**
 * Simple wrapper around {@link Number} that tracks the used radix base.
 *
 * @author Marcel Haßlinger
 */
public class RadixNumber {

    /**
     * Referenced number.
     */
    private final Number number;

    /**
     * Radix base to use.
     * <p>
     * Supported values are:
     * <ul>
     *     <li>Binary: <code>2</code></li>
     *     <li>Octal: <code>8</code></li>
     *     <li>Decimal: <code>10</code></li>
     *     <li>Hex: <code>16</code></li>
     * </ol>
     */
    private final int radix;

    public RadixNumber(Number number, int radix) {
        this.number = number;
        this.radix = radix;
    }

    public Number getNumber() {
        return number;
    }

    public int getRadix() {
        return radix;
    }

    @Override
    public String toString() {
        return "RadixNumber{" +
            "number=" + number +
            ", radix=" + radix +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RadixNumber that = (RadixNumber) o;
        return radix == that.radix && Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, radix);
    }
}

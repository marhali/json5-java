/*
 * Copyright (C) 2022 Marcel Haßlinger
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

package de.marhali.json5;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A class representing a hexadecimal json5 value. Hex values will be stored as {@link BigInteger} internally.
 *
 * @author Marcel Haßlinger
 */
public final class Json5Hexadecimal extends Json5Primitive {
    /**
     * Creates a primitive containing a hex value.
     *
     * @param hex the value to create the primitive with.
     */
    public Json5Hexadecimal(BigInteger hex) {
        super(hex);
    }

    /**
     * Creates a primitive containing a hex value. For String to Number conversion see {@link #parseHexString(String)}
     *
     * @param hex the value to create the primitive with.
     */
    public Json5Hexadecimal(String hex) {
        super(parseHexString(hex));
    }

    /**
     * Converts the provided hex string into it's number representation.
     * Allowed is the character representation of a hex key. Format must be: 0x..., +0x... or -0x...
     *
     * @param hex the hexadecimal value including prefix
     * @return Number representation of hexadecimal string
     */
    public static BigInteger parseHexString(String hex) {
        Objects.requireNonNull(hex);

        switch (hex.charAt(0)) {
            case '+': // +0x...
                return new BigInteger(hex.substring(3), 16);
            case '-': // -0x...
                return new BigInteger(hex.substring(3), 16).negate();
            default: // 0x...
                return new BigInteger(hex.substring(2), 16);
        }
    }

    /**
     * Converts the provided number into it's hex literal character representation.
     *
     * @param bigInteger the number value
     * @param prefixPositive Prefix positive values with {@code +0x...} if true otherwise {@code 0x...}.
     * @return Hex character string including prefix
     */
    public static String serializeHexString(BigInteger bigInteger, boolean prefixPositive) {
        Objects.requireNonNull(bigInteger);

        if (bigInteger.signum() >= 0) {
            return (prefixPositive ? "+0x" : "0x") + bigInteger.toString(16);
        } else {
            return "-0x" + bigInteger.abs().toString(16);
        }
    }

    @Override
    public Json5Element deepCopy() {
        Json5Hexadecimal o = new Json5Hexadecimal((BigInteger) value);
        o.setComment(getComment());
        return o;
    }

    @Override
    public Json5Element noCommentCopy() {
        return new Json5Hexadecimal((BigInteger) value);
    }

    /**
     * Constructs the string representation of the stored hex value.
     *
     * @return Hex value as character literal.
     */
    @Override
    public String getAsString() {
        return serializeHexString(super.getAsBigInteger(), false);
    }
}

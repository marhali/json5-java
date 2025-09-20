/*
 * Copyright (C) 2008 Google Inc.
 * Copyright (C) 2022 - 2025 Marcel Haßlinger
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

import de.marhali.json5.internal.LazilyParsedNumber;
import de.marhali.json5.internal.NumberLimits;
import de.marhali.json5.internal.RadixNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Objects;

/**
 * A class representing a Json5 primitive value. A primitive value is either a String, a Java
 * primitive, or a Java primitive wrapper type.
 *
 * <p>See the {@link Json5Element} documentation for details on how to convert {@code Json5Primitive}
 * and generally any {@code Json5Element} from and to Json5.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @author Marcel Haßlinger
 */
public final class Json5Primitive extends Json5Element {

    private final Object value;

    /**
     * Create a primitive containing a {@code null} value.
     */
    public static Json5Null fromNull() {
        return new Json5Null();
    }

    /**
     * Create a primitive containing a boolean value.
     *
     * @param bool the value to create the primitive with.
     */
    public static Json5Primitive fromBoolean(Boolean bool) {
        return new Json5Primitive(Objects.requireNonNull(bool));
    }

    /**
     * Create a primitive containing a {@link Instant} value.
     * @param instant the value to create the primitive with.
     * <p>
     * <i>This is an extension that is not compliant to the official Json5 spec.</i>
     */
    public static Json5Primitive fromInstant(Instant instant) {
        return new Json5Primitive(Objects.requireNonNull(instant));
    }

    /**
     * Creates a primitive containing a {@link Number} with specified radix base.
     * If a radix base of {@code 2}, {@code 8} or {@code 16} is set,
     * this method will ensure that the underlying number implementation is a {@link BigInteger}.
     * @param number The number
     * @param radix Radix base
     */
    public static Json5Primitive fromNumber(Number number, int radix) {
        Objects.requireNonNull(number);

        if ((radix == 2 || radix == 8 || radix == 16) && !(number instanceof BigInteger)) {
            // Ensure that every binary, octal or hex number is stored as a big integer
            return new Json5Primitive(new RadixNumber(BigInteger.valueOf(number.longValue()), radix));
        }

        return new Json5Primitive(new RadixNumber(number, radix));
    }

    /**
     * Create a primitive containing a decimal {@link Number} (radix base {@code 10}).
     *
     * @param number the value to create the primitive with.
     */
    public static Json5Primitive fromNumber(Number number) {
        return Json5Primitive.fromNumber(Objects.requireNonNull(number), 10);
    }

    /**
     * Create a primitive containing a binary number (radix base {@code 2}).
     * For example {@code +0b1010...}, {@code 0b1010...} or {@code -0b1010...}.
     * <p>
     * <i>This is an extension that is not compliant to the official Json5 spec.</i>
     * @param binaryString the value to create the primitive with.
     */
    public static Json5Primitive fromBinaryString(String binaryString) {
        Objects.requireNonNull(binaryString);

        BigInteger hexInteger;

        switch (binaryString.charAt(0)) {
            case '+': // +0b...
                hexInteger = new BigInteger(binaryString.substring(3), 2);
                break;
            case '-': // -0b...
                hexInteger = new BigInteger(binaryString.substring(3), 2).negate();
                break;
            default: // 0b...
                hexInteger = new BigInteger(binaryString.substring(2), 2);
                break;
        }

        return new Json5Primitive(new RadixNumber(hexInteger, 2));
    }

    /**
     * Create a primitive containing an octal number (radix base {@code 8}).
     * For example {@code +0o107...}, {@code 0o107...} or {@code -0o107...}.
     * <p>
     * <i>This is an extension that is not compliant to the official Json5 spec.</i>
     * @param octalString the value to create the primitive with.
     */
    public static Json5Primitive fromOctalString(String octalString) {
        Objects.requireNonNull(octalString);

        BigInteger hexInteger;

        switch (octalString.charAt(0)) {
            case '+': // +0b...
                hexInteger = new BigInteger(octalString.substring(3), 8);
                break;
            case '-': // -0b...
                hexInteger = new BigInteger(octalString.substring(3), 8).negate();
                break;
            default: // 0b...
                hexInteger = new BigInteger(octalString.substring(2), 8);
                break;
        }

        return new Json5Primitive(new RadixNumber(hexInteger, 8));
    }

    /**
     * Create a primitive containing a binary number (radix base {@code 16}).
     * For example {@code +0x09af...}, {@code 0x09af...} or {@code -0x09af...}.
     * @param hexString the value to create the primitive with.
     */
    public static Json5Primitive fromHexString(String hexString) {
        Objects.requireNonNull(hexString);

        BigInteger hexInteger;

        switch (hexString.charAt(0)) {
            case '+': // +0x...
                hexInteger = new BigInteger(hexString.substring(3), 16);
                break;
            case '-': // -0x...
                hexInteger = new BigInteger(hexString.substring(3), 16).negate();
                break;
            default: // 0x...
                hexInteger = new BigInteger(hexString.substring(2), 16);
                break;
        }

        return new Json5Primitive(new RadixNumber(hexInteger, 16));
    }

    /**
     * Create a primitive containing a String value.
     *
     * @param string the value to create the primitive with.
     */
    public static Json5Primitive fromString(String string) {
        return new Json5Primitive(Objects.requireNonNull(string));
    }

    /**
     * Create a primitive containing a character. The character is turned into a one character String
     * since Json5 only supports String.
     *
     * @param c the value to create the primitive with.
     */
    public static Json5Primitive fromCharacter(Character c) {
        // convert characters to strings since in Json5, characters are represented as a single
        // character string
        return new Json5Primitive(Objects.requireNonNull(c).toString());
    }

    /**
     * Internal constructor with primitive value
     * @param value Internal value
     */
    private Json5Primitive(Object value) {
        this.value = value;
    }

    /**
     * Returns the same value as primitives are immutable.
     */
    @Override
    public Json5Primitive deepCopy() {
        Json5Primitive copy = new Json5Primitive(value);
        copy.setComment(comment);
        return copy;
    }

    /**
     * Check whether this primitive contains a boolean value.
     *
     * @return true if this primitive contains a boolean value, false otherwise.
     */
    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    /**
     * Convenience method to get this element as a boolean value. If this primitive {@linkplain
     * #isBoolean() is not a boolean}, the string value is parsed using {@link
     * Boolean#parseBoolean(String)}. This means {@code "true"} (ignoring case) is considered {@code
     * true} and any other value is considered {@code false}.
     */
    @Override
    public boolean getAsBoolean() {
        if (isBoolean()) {
            return (Boolean) value;
        }
        // Check to see if the value as a String is "true" in any case.
        return Boolean.parseBoolean(getAsString());
    }

    /**
     * Check whether this primitive contains a {@link Instant} value.
     *
     * @return true if this primitive contains a {@link Instant} value, false otherwise.
     */
    public boolean isInstant() {
        return value instanceof Instant;
    }

    @Override
    public Instant getAsInstant() {
        if (isInstant()) {
            return (Instant) value;
        } else if (isString()) {
            return Instant.parse((String) value);
        } else if (isNumber()) {
            var radixNumber = getAsRadixNumber();
            var number = radixNumber.getNumber();

            if(number instanceof Byte || number instanceof Short || number instanceof Integer || number instanceof Long)
                return Instant.ofEpochSecond((long) value);

            if (number instanceof BigInteger)
                return Instant.ofEpochSecond(((BigInteger) number).longValueExact());
        }
        throw new UnsupportedOperationException("Primitive is neither a number nor a string");
    }

    /**
     * Check whether this primitive contains a Number.
     *
     * @return true if this primitive contains a Number, false otherwise.
     */
    public boolean isNumber() {
        return value instanceof RadixNumber;
    }

    @Override
    public RadixNumber getAsRadixNumber() {
        if (isNumber()) {
            return (RadixNumber) value;
        }
        throw new UnsupportedOperationException("Primitive is not a number");
    }

    public int getNumberRadix() {
       return getAsRadixNumber().getRadix();
    }

    public boolean isBinaryNumber() {
        return isNumber() && getNumberRadix() == 2;
    }

    public boolean isOctalNumber() {
        return isNumber() && getNumberRadix() == 8;
    }

    public boolean isHexNumber() {
        return isNumber() && getNumberRadix() == 16;
    }

    /**
     * Convenience method to get this element as a {@link Number}. If this primitive {@linkplain
     * #isString() is a string}, a lazily parsed {@code Number} is constructed which parses the string
     * when any of its methods are called (which can lead to a {@link NumberFormatException}).
     *
     * @throws UnsupportedOperationException if this primitive is neither a number nor a string.
     */
    @Override
    public Number getAsNumber() {
        if (isNumber()) {
            return getAsRadixNumber().getNumber();
        } else if (isString()) {
            return new LazilyParsedNumber((String) value);
        }
        throw new UnsupportedOperationException("Primitive is neither a number nor a string");
    }

    @Override
    public String getAsBinaryString() {
        BigInteger bigInteger = getAsBigInteger();

        if (bigInteger.signum() >= 0) {
            return "0b" + bigInteger.toString(2);
        } else {
            return "-0b" + bigInteger.abs().toString(2);
        }
    }

    @Override
    public String getAsOctalString() {
        BigInteger bigInteger = getAsBigInteger();

        if (bigInteger.signum() >= 0) {
            return "0o" + bigInteger.toString(8);
        } else {
            return "-0o" + bigInteger.abs().toString(8);
        }
    }

    @Override
    public String getAsHexString() {
        BigInteger bigInteger = getAsBigInteger();

        if (bigInteger.signum() >= 0) {
            return "0x" + bigInteger.toString(16);
        } else {
            return "-0x" + bigInteger.abs().toString(16);
        }
    }

    /**
     * Check whether this primitive contains a String value.
     *
     * @return true if this primitive contains a String value, false otherwise.
     */
    public boolean isString() {
        return value instanceof String;
    }

    // Don't add Javadoc, inherit it from super implementation; no exceptions are thrown here
    @Override
    public String getAsString() {
        if (isString()) {
            return (String) value;
        } else if (isInstant()) {
            return ((Instant) value).toString();
        } else if (isBoolean()) {
            return ((Boolean) value).toString();
        } else if (isNumber()) {
            if (isBinaryNumber()) {
                return getAsBinaryString();
            } else if (isOctalNumber()) {
                return getAsOctalString();
            } else if (isHexNumber()) {
                return getAsHexString();
            } else {
                return getAsNumber().toString();
            }
        }

        throw new AssertionError("Unexpected value type: " + value.getClass());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        if (isNumber()) {
            var number = getAsRadixNumber().getNumber();
            if (number instanceof BigDecimal) {
                return (BigDecimal) number;
            }
        }

        return NumberLimits.parseBigDecimal(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public BigInteger getAsBigInteger() {
        if (isNumber()) {
            var number = getAsRadixNumber().getNumber();
            if (number instanceof BigInteger) {
                if (isIntegral(this)) {
                    return BigInteger.valueOf(number.longValue());
                }
            }
        }

        return NumberLimits.parseBigInteger(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }

    /**
     * Convenience method to get this element as a primitive long.
     *
     * @return this element as a primitive long.
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public int getAsInt() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }

    /**
     * @throws NumberFormatException {@inheritDoc}
     */
    @Override
    public byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Json5Primitive that = (Json5Primitive) o;
        return Objects.equals(value, that.value);
    }

    /**
     * Returns true if the specified number is an integral type (Long, Integer, Short, Byte,
     * BigInteger)
     */
    private static boolean isIntegral(Json5Primitive primitive) {
        if (primitive.value instanceof RadixNumber) {
            Number number = ((RadixNumber) primitive.value).getNumber();
            return number instanceof BigInteger
                || number instanceof Long
                || number instanceof Integer
                || number instanceof Short
                || number instanceof Byte;
        }
        return false;
    }
}

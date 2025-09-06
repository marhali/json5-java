/*
 * Copyright (C) 2008 Google Inc.
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

import de.marhali.json5.internal.LazilyParsedNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A class representing a Json primitive value. A primitive value
 * is either a String, a Hexadecimal, a Java primitive, or a Java primitive
 * wrapper type.
 *
 * @author Marcel Haßlinger
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public abstract class Json5Primitive extends Json5Element {
    protected final Object value;

    public Json5Primitive(Object value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Quick creator for a primitive with boolean value.
     *
     * @param value Boolean value to apply.
     * @return Corresponding primitive with provided value.
     */
    public static Json5Primitive of(Boolean value) {
        return new Json5Boolean(value);
    }

    /**
     * Quick creator for a primitive with number value.
     *
     * @param value Number value to apply.
     * @return Corresponding primitive with provided value.
     */
    public static Json5Primitive of(Number value) {
        return new Json5Number(value);
    }

    /**
     * Quick creator for a primitive with string value.
     * Set hexadecimal to true to receive a {@link Json5Hexadecimal}.
     *
     * @param value String value to apply.
     * @param hexadecimal Is the provided value a hex string literal?
     * @return Corresponding primitive with provided value.
     */
    public static Json5Primitive of(String value, boolean hexadecimal) {
        return hexadecimal ? new Json5Hexadecimal(value) : new Json5String(value);
    }

    /**
     * Quick creator for a primitive with string value.
     *
     * @param value String value to apply.
     * @return Corresponding primitive with provided value.
     */
    public static Json5Primitive of(String value) {
        return new Json5String(value);
    }

    /**
     * convenience method to get this element as a boolean value.
     *
     * @return get this element as a primitive boolean value.
     */
    @Override
    public boolean getAsBoolean() {
        if (isBoolean()) {
            return ((Boolean) value).booleanValue();
        }
        // Check to see if the value as a String is "true" in any case.
        return Boolean.parseBoolean(getAsString());
    }

    /**
     * convenience method to get this element as a Number.
     *
     * @return get this element as a Number.
     * @throws NumberFormatException if the value contained is not a valid Number.
     */
    @Override
    public Number getAsNumber() {
        return value instanceof String ? new LazilyParsedNumber((String) value) : (Number) value;
    }

    /**
     * convenience method to get this element as a String.
     *
     * @return get this element as a String.
     */
    @Override
    public String getAsString() {
        if (isNumber()) {
            return getAsNumber().toString();
        } else if (isBoolean()) {
            return ((Boolean) value).toString();
        } else {
            return (String) value;
        }
    }

    /**
     * convenience method to get this element as a primitive double.
     *
     * @return get this element as a primitive double.
     * @throws NumberFormatException if the value contained is not a valid double.
     */
    @Override
    public double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    /**
     * convenience method to get this element as a float.
     *
     * @return get this element as a float.
     * @throws NumberFormatException if the value contained is not a valid float.
     */
    @Override
    public float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }

    /**
     * convenience method to get this element as a primitive long.
     *
     * @return get this element as a primitive long.
     * @throws NumberFormatException if the value contained is not a valid long.
     */
    @Override
    public long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    /**
     * convenience method to get this element as a primitive integer.
     *
     * @return get this element as a primitive integer.
     * @throws NumberFormatException if the value contained is not a valid integer.
     */
    @Override
    public int getAsInt() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }

    @Override
    public byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }

    /**
     * convenience method to get this element as a {@link BigDecimal}.
     *
     * @return get this element as a {@link BigDecimal}.
     * @throws NumberFormatException if the value contained is not a valid {@link BigDecimal}.
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }

    /**
     * convenience method to get this element as a {@link BigInteger}.
     *
     * @return get this element as a {@link BigInteger}.
     * @throws NumberFormatException if the value contained is not a valid {@link BigInteger}.
     */
    @Override
    public BigInteger getAsBigInteger() {
        return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
    }

    /**
     * convenience method to get this element as a primitive short.
     *
     * @return get this element as a primitive short.
     * @throws NumberFormatException if the value contained is not a valid short value.
     */
    @Override
    public short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
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
     * Check whether this primitive contains a Number.
     *
     * @return true if this primitive contains a Number, false otherwise.
     */
    public boolean isNumber() {
        return value instanceof Number;
    }

    /**
     * Check whether this primitive contains a String value.
     *
     * @return true if this primitive contains a String value, false otherwise.
     */
    public boolean isString() {
        return value instanceof String;
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return 31;
        }
        // Using the recommended hashing algorithm from Effective Java for longs and doubles
        if (isIntegral(this)) {
            long value = getAsNumber().longValue();
            return (int) (value ^ (value >>> 32));
        }
        if (value instanceof Number) {
            long value = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) (value ^ (value >>> 32));
        }
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Json5Primitive other = (Json5Primitive) obj;
        if (value == null) {
            return other.value == null;
        }
        if (isIntegral(this) && isIntegral(other)) {
            return getAsNumber().longValue() == other.getAsNumber().longValue();
        }
        if (value instanceof Number && other.value instanceof Number) {
            double a = getAsNumber().doubleValue();
            // Java standard types other than double return true for two NaN. So, need
            // special handling for double.
            double b = other.getAsNumber().doubleValue();
            return a == b || (Double.isNaN(a) && Double.isNaN(b));
        }
        return value.equals(other.value);
    }

    /**
     * Returns true if the specified number is an integral type
     * (Long, Integer, Short, Byte, BigInteger)
     */
    private static boolean isIntegral(Json5Primitive primitive) {
        if (primitive.value instanceof Number) {
            Number number = (Number) primitive.value;
            return number instanceof BigInteger || number instanceof Long || number instanceof Integer
                || number instanceof Short || number instanceof Byte;
        }
        return false;
    }
}

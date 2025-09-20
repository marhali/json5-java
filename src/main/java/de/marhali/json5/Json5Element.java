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

import de.marhali.json5.config.Json5Options;
import de.marhali.json5.internal.RadixNumber;
import de.marhali.json5.stream.Json5Writer;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Objects;

/**
 * A class representing an element of Json5. It could either be a {@link Json5Object}, a {@link
 * Json5Array}, a {@link Json5Primitive} or a {@link Json5Null}.
 *
 * <p>This class provides multiple {@code getAs} methods which allow
 *
 * <ul>
 *   <li>obtaining the represented primitive value, for example {@link #getAsString()}
 *   <li>casting to the {@code Json5Element} subclasses in a convenient way, for example {@link
 *       #getAsJson5Object()}
 * </ul>
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @author Marcel Haßlinger
 */
public abstract class Json5Element {
    /**
     * Associated comment on this element. Can be <code>null</code> to omit.
     * Supports multi-line comments by using the break-line control character <code>\n</code>.
     */
    protected String comment;

    /**
     * Provides a check for verifying if this element has an associated comment.
     * @return true if this element has an associated comment, false otherwise.
     */
    public boolean hasComment() {
        return this.comment != null;
    }

    /**
     * Returns the associated comment on this element. Can be <code>null</code> if not set.
     *
     * @return optional comment string
     */
    public String getComment() {
        return comment;
    }

    /**
     * Updates the associated comment on this element.
     * Supports multi-line comments with break-line control character.
     *
     * @param comment Comment to set. Can be <code>null</code> to omit.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Returns a deep copy of this element.
     */
    public abstract Json5Element deepCopy();

    /**
     * Provides a check for verifying if this element is a Json5 array or not.
     *
     * @return true if this element is of type {@link Json5Array}, false otherwise.
     */
    public boolean isJson5Array() {
        return this instanceof Json5Array;
    }

    /**
     * Provides a check for verifying if this element is a Json5 object or not.
     *
     * @return true if this element is of type {@link Json5Object}, false otherwise.
     */
    public boolean isJson5Object() {
        return this instanceof Json5Object;
    }

    /**
     * Provides a check for verifying if this element is a primitive or not.
     *
     * @return true if this element is of type {@link Json5Primitive}, false otherwise.
     */
    public boolean isJson5Primitive() {
        return this instanceof Json5Primitive;
    }

    /**
     * Provides a check for verifying if this element represents a null value or not.
     *
     * @return true if this element is of type {@link Json5Null}, false otherwise.
     */
    public boolean isJson5Null() {
        return this instanceof Json5Null;
    }

    /**
     * Convenience method to get this element as a {@link Json5Object}. If this element is of some
     * other type, an {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJson5Object()}
     * first.
     *
     * @return this element as a {@link Json5Object}.
     * @throws IllegalStateException if this element is of another type.
     */
    public Json5Object getAsJson5Object() {
        if (isJson5Object()) {
            return (Json5Object) this;
        }
        throw new IllegalStateException("Not a Json5Object: " + this);
    }

    /**
     * Convenience method to get this element as a {@link Json5Array}. If this element is of some other
     * type, an {@link IllegalStateException} will result. Hence it is best to use this method after
     * ensuring that this element is of the desired type by calling {@link #isJson5Array()} first.
     *
     * @return this element as a {@link Json5Array}.
     * @throws IllegalStateException if this element is of another type.
     */
    public Json5Array getAsJson5Array() {
        if (isJson5Array()) {
            return (Json5Array) this;
        }
        throw new IllegalStateException("Not a Json5Array: " + this);
    }

    /**
     * Convenience method to get this element as a {@link Json5Primitive}. If this element is of some
     * other type, an {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJson5Primitive()}
     * first.
     *
     * @return this element as a {@link Json5Primitive}.
     * @throws IllegalStateException if this element is of another type.
     */
    public Json5Primitive getAsJson5Primitive() {
        if (isJson5Primitive()) {
            return (Json5Primitive) this;
        }
        throw new IllegalStateException("Not a Json5Primitive: " + this);
    }

    /**
     * Convenience method to get this element as a {@link Json5Null}. If this element is of some other
     * type, an {@link IllegalStateException} will result. Hence it is best to use this method after
     * ensuring that this element is of the desired type by calling {@link #isJson5Null()} first.
     *
     * @return this element as a {@link Json5Null}.
     * @throws IllegalStateException if this element is of another type.
     */
    public Json5Null getAsJson5Null() {
        if (isJson5Null()) {
            return (Json5Null) this;
        }
        throw new IllegalStateException("Not a Json5Null: " + this);
    }

    /**
     * Convenience method to get this element as a boolean value.
     *
     * @return this element as a primitive boolean value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a {@link Instant} value.
     *
     * @return this element as a primitive {@link Instant} value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public Instant getAsInstant() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a {@link Number}.
     *
     * @return this element as a {@link Number}.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}, or cannot be converted to a number.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public Number getAsNumber() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a {@link RadixNumber}.
     *
     * @return this element as a {@link RadixNumber}.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}, or cannot be converted to a radix number.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public RadixNumber getAsRadixNumber() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a string value.
     *
     * @return this element as a string value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public String getAsString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive double value.
     *
     * @return this element as a primitive double value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid double.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive float value.
     *
     * @return this element as a primitive float value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid float.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public float getAsFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive long value.
     *
     * @return this element as a primitive long value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid long.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive integer value.
     *
     * @return this element as a primitive integer value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid integer.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive byte value.
     *
     * @return this element as a primitive byte value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid byte.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public byte getAsByte() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a {@link BigDecimal}.
     *
     * @return this element as a {@link BigDecimal}.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if this element is not a valid {@link BigDecimal}.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a {@link BigInteger}.
     *
     * @return this element as a {@link BigInteger}.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if this element is not a valid {@link BigInteger}.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive short value.
     *
     * @return this element as a primitive short value.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid short.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public short getAsShort() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Convenience method to get this element as a primitive binary number (radix base {@code 2}) value.
     * <p>
     * <i>This is an extension that is not compliant to the official Json5 spec.</i>
     * @return this element as a primitive binary number value string.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid short.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public String getAsBinaryString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

    /**
     * Convenience method to get this element as a primitive octal number (radix base {@code 8}) value.
     * <p>
     * <i>This is an extension that is not compliant to the official Json5 spec.</i>
     * @return this element as a primitive octal number value string.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid short.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public String getAsOctalString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());

    }

    /**
     * Convenience method to get this element as a primitive hex number (radix base {@code 16}) value.
     * @return this element as a primitive hex number value string.
     * @throws UnsupportedOperationException if this element is not a {@link Json5Primitive} or {@link
     *     Json5Array}.
     * @throws NumberFormatException if the value contained is not a valid short.
     * @throws IllegalStateException if this element is of the type {@link Json5Array} but contains
     *     more than a single element.
     */
    public String getAsHexString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Converts this element to a Json5 string using the provided configuration options for formatting.
     * @param options Configuration options.
     * @return Json5 string representation of this element.
     */
    public String toString(Json5Options options) {
        Objects.requireNonNull(options);

        try {
            StringWriter stringWriter = new StringWriter();
            Json5Writer json5Writer = new Json5Writer(options, stringWriter);
            json5Writer.write(this);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Converts this element to a Json5 string.
     *
     * <p>For example:
     *
     * <pre>
     * Json5Object object = new Json5Object();
     * object.add("a", new Json5Null());
     * Json5Array array = new Json5Array();
     * array.add(1);
     * object.add("b", array);
     *
     * String json = object.toString();
     * // json: {"a":null,"b":[1]}
     * </pre>
     *
     * <p>To get the contained String value (without enclosing {@code "} and without escaping), use
     * {@link #getAsString()} instead:
     *
     * <pre>
     * Json5Primitive Json5Primitive = new Json5Primitive("with \" quote");
     * String json = Json5Primitive.toString();
     * // json: "with \" quote"
     * String value = Json5Primitive.getAsString();
     * // value: with " quote
     * </pre>
     *
     * @see #toString(Json5Options)
     */
    @Override
    public String toString() {
        return toString(Json5Options.DEFAULT);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Json5Element that = (Json5Element) o;
        return Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(comment);
    }
}

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

import de.marhali.json5.stream.Json5Writer;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * A class representing an element of Json5. It could either be a {@link Json5Object}, a
 * {@link Json5Array}, a {@link Json5Primitive} or a {@link Json5Null}.
 *
 * @author Marcel Haßlinger
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public abstract class Json5Element {
    /**
     * Returns a deep copy of this element. Immutable elements like primitives
     * and nulls are not copied.
     * @since 2.8.2
     */
    public abstract Json5Element deepCopy();

    /**
     * provides check for verifying if this element is an array or not.
     *
     * @return true if this element is of type {@link Json5Array}, false otherwise.
     */
    public boolean isJsonArray() {
        return this instanceof Json5Array;
    }

    /**
     * provides check for verifying if this element is a Json object or not.
     *
     * @return true if this element is of type {@link Json5Object}, false otherwise.
     */
    public boolean isJsonObject() {
        return this instanceof Json5Object;
    }

    /**
     * provides check for verifying if this element is a primitive or not.
     *
     * @return true if this element is of type {@link Json5Primitive}, false otherwise.
     */
    public boolean isJsonPrimitive() {
        return this instanceof Json5Primitive;
    }

    /**
     * provides check for verifying if this element represents a null value or not.
     *
     * @return true if this element is of type {@link Json5Null}, false otherwise.
     * @since 1.2
     */
    public boolean isJsonNull() {
        return this instanceof Json5Null;
    }

    /**
     * convenience method to get this element as a {@link Json5Object}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJsonObject()}
     * first.
     *
     * @return get this element as a {@link Json5Object}.
     * @throws IllegalStateException if the element is of another type.
     */
    public Json5Object getAsJsonObject() {
        if (isJsonObject()) {
            return (Json5Object) this;
        }
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    /**
     * convenience method to get this element as a {@link Json5Array}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJsonArray()}
     * first.
     *
     * @return get this element as a {@link Json5Array}.
     * @throws IllegalStateException if the element is of another type.
     */
    public Json5Array getAsJsonArray() {
        if (isJsonArray()) {
            return (Json5Array) this;
        }
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    /**
     * convenience method to get this element as a {@link Json5Primitive}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJsonPrimitive()}
     * first.
     *
     * @return get this element as a {@link Json5Primitive}.
     * @throws IllegalStateException if the element is of another type.
     */
    public Json5Primitive getAsJsonPrimitive() {
        if (isJsonPrimitive()) {
            return (Json5Primitive) this;
        }
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    /**
     * convenience method to get this element as a {@link Json5Null}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJsonNull()}
     * first.
     *
     * @return get this element as a {@link Json5Null}.
     * @throws IllegalStateException if the element is of another type.
     * @since 1.2
     */
    public Json5Null getAsJsonNull() {
        if (isJsonNull()) {
            return (Json5Null) this;
        }
        throw new IllegalStateException("Not a JSON Null: " + this);
    }

    /**
     * convenience method to get this element as a boolean value.
     *
     * @return get this element as a primitive boolean value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * boolean value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a {@link Number}.
     *
     * @return get this element as a {@link Number}.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * number.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public Number getAsNumber() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a string value.
     *
     * @return get this element as a string value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * string value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public String getAsString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a primitive double value.
     *
     * @return get this element as a primitive double value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * double value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a primitive float value.
     *
     * @return get this element as a primitive float value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * float value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public float getAsFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a primitive long value.
     *
     * @return get this element as a primitive long value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * long value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a primitive integer value.
     *
     * @return get this element as a primitive integer value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * integer value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a primitive byte value.
     *
     * @return get this element as a primitive byte value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * byte value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     * @since 1.3
     */
    public byte getAsByte() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a {@link BigDecimal}.
     *
     * @return get this element as a {@link BigDecimal}.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive}.
     * * @throws NumberFormatException if the element is not a valid {@link BigDecimal}.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     * @since 1.2
     */
    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a {@link BigInteger}.
     *
     * @return get this element as a {@link BigInteger}.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive}.
     * @throws NumberFormatException if the element is not a valid {@link BigInteger}.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     * @since 1.2
     */
    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * convenience method to get this element as a primitive short value.
     *
     * @return get this element as a primitive short value.
     * @throws ClassCastException if the element is of not a {@link Json5Primitive} and is not a valid
     * short value.
     * @throws IllegalStateException if the element is of the type {@link Json5Array} but contains
     * more than a single element.
     */
    public short getAsShort() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * Returns a simple String representation of this element.
     * For pretty-printing use {@link Json5Writer} with custom configuration options.
     * @see #toString(Json5Options)
     */
    @Override
    public String toString() {
        return toString(Json5Options.DEFAULT);
    }

    /**
     * Returns the String representation of this element.
     * @param options Configured serialization behaviour
     * @return Stringified representation of this element
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
}
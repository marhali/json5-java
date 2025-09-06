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
    private String comment;

    /**
     * Gets the comment associated with this element.
     *
     * @return The comment string, or null if none exists.
     */
    public String getComment() {
        return this.comment;
    }

    /**
     * 对于Json5Null对象的注释,请通过以下方法来set
     * @see Json5Object#setComment(String, String)
     * @see Json5Array#setComment(int, String)
     * comment字段不会参与equals 和hashCode
     *
     * @param comment The comment string. Can be multi-line.
     */
    public void setComment(String comment) {
        // 不应该直接操作Json5Null.INSTANCE实例的注释
        // 应该通过JsonObject或Json5Array的 setComment方法来操作
        if (this == Json5Null.INSTANCE) {
            return;
        }
        this.comment = comment;
    }

    /**
     * Checks if a comment is associated with this element.
     *
     * @return True if a comment exists, false otherwise.
     */
    public boolean hasComment() {
        return this.comment != null;
    }

    /**
     * @return 深拷贝对象,包含各个子元素的注释
     */
    public abstract Json5Element deepCopy();

    /**
     *
     * @return 返回一个不带任何注释的拷贝对象
     */
    public abstract Json5Element noCommentCopy();

    /**
     * provides check for verifying if this element is an array or not.
     *
     * @return true if this element is of type {@link Json5Array}, false otherwise.
     */
    public boolean isJson5Array() {
        return this instanceof Json5Array;
    }

    /**
     * provides check for verifying if this element is a Json object or not.
     *
     * @return true if this element is of type {@link Json5Object}, false otherwise.
     */
    public boolean isJson5Object() {
        return this instanceof Json5Object;
    }

    /**
     * provides check for verifying if this element is a primitive or not.
     *
     * @return true if this element is of type {@link Json5Primitive}, false otherwise.
     */
    public boolean isJson5Primitive() {
        return this instanceof Json5Primitive;
    }

    /**
     * provides check for verifying if this element represents a null value or not.
     *
     * @return true if this element is of type {@link Json5Null}, false otherwise.
     */
    public boolean isJson5Null() {
        return this instanceof Json5Null;
    }

    /**
     * 当对象为空字符串 空Json5Object 空Json5Array Json5Null时返回True,其它情况返回False.
     *
     * @return 对象是否为空
     */
    public boolean isEmpty() {
        if (this instanceof Json5Null) {
            return true;
        } else if (this instanceof Json5Object) {
            // noinspection RedundantCast
            return ((Json5Object) this).isEmpty();
        } else if (this instanceof Json5Array) {
            // noinspection RedundantCast
            return ((Json5Array) this).isEmpty();
        } else if (this instanceof Json5String) {
            // noinspection RedundantCast
            return ((Json5String) this).isEmpty();
        }
        return false;
    }

    /**
     * 将本对象的注释复制到目标对象
     *
     * @param target 目标对象
     * @see Json5Object#mergeCommentTo(Json5Object)
     * @see Json5Array#mergeCommentTo(Json5Array)
     */
    public void copyCommentTo(Json5Element target) {
        if (target != null && this.comment != null) {
            target.setComment(this.comment);
        }
    }

    /**
     * convenience method to get this element as a {@link Json5Object}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJson5Object()}
     * first.
     *
     * @return get this element as a {@link Json5Object}.
     * @throws IllegalStateException if the element is of another type.
     */
    public Json5Object getAsJson5Object() {
        if (isJson5Object()) {
            return (Json5Object) this;
        }
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    /**
     * convenience method to get this element as a {@link Json5Array}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJson5Array()}
     * first.
     *
     * @return get this element as a {@link Json5Array}.
     * @throws IllegalStateException if the element is of another type.
     */
    public Json5Array getAsJson5Array() {
        if (isJson5Array()) {
            return (Json5Array) this;
        }
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    /**
     * convenience method to get this element as a {@link Json5Primitive}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJson5Primitive()}
     * first.
     *
     * @return get this element as a {@link Json5Primitive}.
     * @throws IllegalStateException if the element is of another type.
     */
    public Json5Primitive getAsJson5Primitive() {
        if (isJson5Primitive()) {
            return (Json5Primitive) this;
        }
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    /**
     * convenience method to get this element as a {@link Json5Null}. If the element is of some
     * other type, a {@link IllegalStateException} will result. Hence it is best to use this method
     * after ensuring that this element is of the desired type by calling {@link #isJson5Null()}
     * first.
     *
     * @return get this element as a {@link Json5Null}.
     * @throws IllegalStateException if the element is of another type.
     */
    @SuppressWarnings("unused")
    public Json5Null getAsJson5Null() {
        if (isJson5Null()) {
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
     *
     * @see #toString(Json5Options)
     */
    @Override
    public String toString() {
        return toString(Json5Options.DEFAULT);
    }

    /**
     * 将对象转为无注释标准json字符串
     * @return 压缩的json字符串
     */
    public String toStandardString() {
        return toString(new Json5OptionsBuilder().notWriteComments().build());
    }

    /**
     * Returns the String representation of this element.
     *
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
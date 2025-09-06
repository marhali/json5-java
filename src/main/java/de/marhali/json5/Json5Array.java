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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class representing an array type in Json5. An array is a list of {@link Json5Element}s each of
 * which can be of a different type. This is an ordered list, meaning that the order in which
 * elements are added is preserved.
 *
 * @author Marcel Haßlinger
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
@SuppressWarnings("unused")
public final class Json5Array extends Json5Element implements Iterable<Json5Element> {
    private final List<Json5Element> elements;

    /**
     * Creates an empty Json5Array.
     */
    public Json5Array() {
        elements = new ArrayList<Json5Element>();
    }

    public Json5Array(int capacity) {
        elements = new ArrayList<Json5Element>(capacity);
    }

    /**
     * Creates a deep copy of this element and all its children
     */
    @Override
    public Json5Array deepCopy() {
        if (!elements.isEmpty()) {
            Json5Array result = new Json5Array(elements.size());
            for (Json5Element element : elements) {
                result.add(element.deepCopy());
            }
            result.setComment(this.getComment());
            return result;
        }
        return new Json5Array();
    }

    @Override
    public Json5Element noCommentCopy() {
        if (!elements.isEmpty()) {
            Json5Array result = new Json5Array(elements.size());
            for (Json5Element element : elements) {
                result.add(element.noCommentCopy());
            }
            return result;
        }
        return new Json5Array();
    }

    /**
     * Returns true if the array is empty
     *
     * @return true if the array is empty
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * convenience method to get this array as a boolean if it contains a single element.
     *
     * @return get this element as a boolean if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid boolean.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public boolean getAsBoolean() {
        if (elements.size() == 1) {
            return elements.get(0).getAsBoolean();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a {@link Number} if it contains a single element.
     *
     * @return get this element as a number if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid Number.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public Number getAsNumber() {
        if (elements.size() == 1) {
            return elements.get(0).getAsNumber();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a {@link String} if it contains a single element.
     *
     * @return get this element as a String if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid String.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public String getAsString() {
        if (elements.size() == 1) {
            return elements.get(0).getAsString();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a double if it contains a single element.
     *
     * @return get this element as a double if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid double.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public double getAsDouble() {
        if (elements.size() == 1) {
            return elements.get(0).getAsDouble();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a float if it contains a single element.
     *
     * @return get this element as a float if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid float.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public float getAsFloat() {
        if (elements.size() == 1) {
            return elements.get(0).getAsFloat();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a long if it contains a single element.
     *
     * @return get this element as a long if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid long.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public long getAsLong() {
        if (elements.size() == 1) {
            return elements.get(0).getAsLong();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as an integer if it contains a single element.
     *
     * @return get this element as an integer if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid integer.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public int getAsInt() {
        if (elements.size() == 1) {
            return elements.get(0).getAsInt();
        }
        throw new IllegalStateException();
    }

    @Override
    public byte getAsByte() {
        if (elements.size() == 1) {
            return elements.get(0).getAsByte();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a {@link BigDecimal} if it contains a single element.
     *
     * @return get this element as a {@link BigDecimal} if it is single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive}.
     * @throws NumberFormatException if the element at index 0 is not a valid {@link BigDecimal}.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        if (elements.size() == 1) {
            return elements.get(0).getAsBigDecimal();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a {@link BigInteger} if it contains a single element.
     *
     * @return get this element as a {@link BigInteger} if it is single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive}.
     * @throws NumberFormatException if the element at index 0 is not a valid {@link BigInteger}.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public BigInteger getAsBigInteger() {
        if (elements.size() == 1) {
            return elements.get(0).getAsBigInteger();
        }
        throw new IllegalStateException();
    }

    /**
     * convenience method to get this array as a primitive short if it contains a single element.
     *
     * @return get this element as a primitive short if it is a single element array.
     * @throws ClassCastException if the element in the array is of not a {@link Json5Primitive} and
     * is not a valid short.
     * @throws IllegalStateException if the array has more than one element.
     */
    @Override
    public short getAsShort() {
        if (elements.size() == 1) {
            return elements.get(0).getAsShort();
        }
        throw new IllegalStateException();
    }

    /**
     * Adds the specified boolean to self.
     *
     * @param bool the boolean that needs to be added to the array.
     */
    public void add(Boolean bool) {
        elements.add(bool == null ? Json5Null.INSTANCE : new Json5Boolean(bool));
    }

    /**
     * Adds the specified character to self.
     *
     * @param character the character that needs to be added to the array.
     */
    public void add(Character character) {
        elements.add(character == null ? Json5Null.INSTANCE : new Json5String(character.toString()));
    }

    /**
     * Adds the specified number to self.
     *
     * @param number the number that needs to be added to the array.
     */
    public void add(Number number) {
        elements.add(number == null ? Json5Null.INSTANCE : new Json5Number(number));
    }

    /**
     * Adds the specified string to self.
     *
     * @param string the string that needs to be added to the array.
     */
    public void add(String string) {
        elements.add(string == null ? Json5Null.INSTANCE : new Json5String(string));
    }

    /**
     * Adds the specified element to self.
     *
     * @param element the element that needs to be added to the array.
     */
    public void add(Json5Element element) {
        if (element == null) {
            element = Json5Null.INSTANCE;
        }
        elements.add(element);
    }

    /**
     * Adds all the elements of the specified array to self.
     *
     * @param array the array whose elements need to be added to the array.
     */
    public void addAll(Json5Array array) {
        elements.addAll(array.elements);
    }

    /**
     * Replaces the element at the specified position in this array with the specified element.
     * Element can be null.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    public Json5Element set(int index, Json5Element element) {
        return elements.set(index, element);
    }

    /**
     * Removes the first occurrence of the specified element from this array, if it is present.
     * If the array does not contain the element, it is unchanged.
     *
     * @param element element to be removed from this array, if present
     * @return true if this array contained the specified element, false otherwise
     */
    public boolean remove(Json5Element element) {
        return elements.remove(element);
    }

    /**
     * Removes the element at the specified position in this array. Shifts any subsequent elements
     * to the left (subtracts one from their indices). Returns the element that was removed from
     * the array.
     *
     * @param index index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    @SuppressWarnings("UnusedReturnValue")
    public Json5Element remove(int index) {
        return elements.remove(index);
    }

    /**
     * Returns true if this array contains the specified element.
     *
     * @param element whose presence in this array is to be tested
     * @return true if this array contains the specified element.
     */
    public boolean contains(Json5Element element) {
        return elements.contains(element);
    }

    /**
     * Returns the number of elements in the array.
     *
     * @return the number of elements in the array.
     */
    public int size() {
        return elements.size();
    }

    /**
     * 将本对象的非空注释覆盖合并到目标对象
     * 取两者长度较短者作为最大遍历长度
     * 要求目标对象的同索引子元素应与本子元素具备类型一致性
     *
     * @param target 目标对象
     */
    public void mergeCommentTo(Json5Array target) {
        super.copyCommentTo(target);
        if (!elements.isEmpty()) {
            int loop = Math.min(elements.size(), target.size());
            for (int i = 0; i < loop; i++) {
                Json5Element element = this.get(i);
                if (!element.hasComment()) {
                    continue;
                }
                if (element instanceof Json5Primitive) {
                    element.copyCommentTo(target.get(i));
                } else if (element instanceof Json5Array) {
                    ((Json5Array) element).mergeCommentTo((Json5Array) target.get(i));
                } else if (element instanceof Json5Object) {
                    ((Json5Object) element).mergeCommentTo((Json5Object) target.get(i));
                }
            }
        }
    }

    /**
     * Sets the comment for an element.
     * comment字段不会参与equals 和hashCode
     *
     * @param index index of elements
     * @param comment comment for the member
     */
    public void setComment(int index, String comment) {
        Json5Element element = get(index);
        if (element instanceof Json5Null) {
            Json5Null json5Null = new Json5Null();
            json5Null.setComment(comment);
            elements.set(index, json5Null);
        } else {
            element.setComment(comment);
        }
    }

    /**
     * Returns an iterator to navigate the elements of the array. Since the array is an ordered list,
     * the iterator navigates the elements in the order they were inserted.
     *
     * @return an iterator to navigate the elements of the array.
     */
    public Iterator<Json5Element> iterator() {
        return elements.iterator();
    }

    /**
     * Returns the ith element of the array.
     *
     * @param i the index of the element that is being sought.
     * @return the element present at the ith index.
     * @throws IndexOutOfBoundsException if i is negative or greater than or equal to the
     * {@link #size()} of the array.
     */
    public Json5Element get(int i) {
        return elements.get(i);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    /**
     * 暂不支持直接转POJO对象,请使用toStandardString然后使用其它反序列化框架处理
     * @see Json5Element#toString(Json5Options)
     * @see Json5Element#toStandardString
     *
     * @param clazz The class of the POJO to convert to.
     * @param <T> The type of the POJO.
     * @return An list instance of the specified POJO class, populated with data from this Json5Array.
     */
    public <T> List<T> toPojoList(Class<T> clazz) throws Exception {
        throw new Exception("不支持的操作!");
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof Json5Array && ((Json5Array) o).elements.equals(elements));
    }
}
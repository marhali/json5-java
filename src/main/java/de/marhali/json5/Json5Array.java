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

import de.marhali.json5.internal.NonNullElementWrapperList;
import de.marhali.json5.internal.RadixNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A class representing an array type in Json5. An array is a list of {@link Json5Element}s each of
 * which can be of a different type. This is an ordered list, meaning that the order in which
 * elements are added is preserved. This class does not support {@code null} values. If {@code null}
 * is provided as element argument to any of the methods, it is converted to a {@link Json5Null}.
 *
 * <p>{@code Json5Array} only implements the {@link Iterable} interface but not the {@link List}
 * interface. A {@code List} view of it can be obtained with {@link #asList()}.
 *
 * <p>See the {@link Json5} documentation for details on how to convert {@code Json5Array} and
 * generally any {@code Json5Element} from and to Json5.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @author Marcel Haßlinger
 */
public final class Json5Array extends Json5Element implements Iterable<Json5Element> {
    private final ArrayList<Json5Element> elements;

    /** Creates an empty Json5Array. */
    public Json5Array() {
        elements = new ArrayList<>();
    }

    /**
     * Creates an empty Json5Array with the desired initial capacity.
     *
     * @param capacity initial capacity.
     * @throws IllegalArgumentException if the {@code capacity} is negative
     */
    public Json5Array(int capacity) {
        elements = new ArrayList<>(capacity);
    }

    /**
     * Creates a deep copy of this element and all its children.
     */
    @Override
    public Json5Array deepCopy() {
        Json5Array result = new Json5Array(elements.size());
        for (Json5Element element : elements) {
            result.add(element.deepCopy());
        }
        result.setComment(comment);
        return result;
    }

    /**
     * Adds the specified boolean to self.
     *
     * @param bool the boolean that needs to be added to the array.
     */
    public void add(Boolean bool) {
        elements.add(bool == null ? new Json5Null() : Json5Primitive.fromBoolean(bool));
    }

    /**
     * Adds the specified character to self.
     *
     * @param character the character that needs to be added to the array.
     */
    public void add(Character character) {
        elements.add(character == null ? new Json5Null() : Json5Primitive.fromCharacter(character));
    }

    /**
     * Adds the specified number to self.
     *
     * @param number the number that needs to be added to the array.
     */
    public void add(Number number) {
        elements.add(number == null ? new Json5Null() : Json5Primitive.fromNumber(number));
    }

    public void add(Number number, int radix) {
        elements.add(number == null ? new Json5Null() : Json5Primitive.fromNumber(number, radix));
    }

    /**
     * Adds the specified string to self.
     *
     * @param string the string that needs to be added to the array.
     */
    public void add(String string) {
        elements.add(string == null ? new Json5Null() : Json5Primitive.fromString(string));
    }

    /**
     * Adds the specified element to self.
     *
     * @param element the element that needs to be added to the array.
     */
    public void add(Json5Element element) {
        if (element == null) {
            element = new Json5Null();
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
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    public Json5Element set(int index, Json5Element element) {
        return elements.set(index, element == null ? new Json5Null() : element);
    }

    /**
     * Removes the first occurrence of the specified element from this array, if it is present. If the
     * array does not contain the element, it is unchanged.
     *
     * @param element element to be removed from this array, if present
     * @return true if this array contained the specified element, false otherwise
     */
    public boolean remove(Json5Element element) {
        return elements.remove(element);
    }

    /**
     * Removes the element at the specified position in this array. Shifts any subsequent elements to
     * the left (subtracts one from their indices). Returns the element that was removed from the
     * array.
     *
     * @param index index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the specified index is outside the array bounds
     */
    public Json5Element remove(int index) {
        return elements.remove(index);
    }

    /**
     * Returns true if this array contains the specified element.
     *
     * @return true if this array contains the specified element.
     * @param element whose presence in this array is to be tested
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
     * Returns true if the array is empty.
     *
     * @return true if the array is empty.
     */
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Returns an iterator to navigate the elements of the array. Since the array is an ordered list,
     * the iterator navigates the elements in the order they were inserted.
     *
     * @return an iterator to navigate the elements of the array.
     */
    @Override
    public Iterator<Json5Element> iterator() {
        return elements.iterator();
    }

    /**
     * Returns the i-th element of the array.
     *
     * @param i the index of the element that is being sought.
     * @return the element present at the i-th index.
     * @throws IndexOutOfBoundsException if {@code i} is negative or greater than or equal to the
     *     {@link #size()} of the array.
     */
    public Json5Element get(int i) {
        return elements.get(i);
    }

    private Json5Element getAsSingleElement() {
        int size = elements.size();
        if (size == 1) {
            return elements.get(0);
        }
        throw new IllegalStateException("Array must have size 1, but has size " + size);
    }

    @Override
    public Json5Null getAsJson5Null() {
        return getAsSingleElement().getAsJson5Null();
    }

    /**
     * Convenience method to get this array as a {@link Number} if it contains a single element. This
     * method calls {@link Json5Element#getAsNumber()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a number if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public Number getAsNumber() {
        return getAsSingleElement().getAsNumber();
    }

    /**
     * Convenience method to get this array as a {@link RadixNumber} if it contains a single element. This
     * method calls {@link Json5Element#getAsRadixNumber()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a radix number if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public RadixNumber getAsRadixNumber() {
        return getAsSingleElement().getAsRadixNumber();
    }

    /**
     * Convenience method to get this array as a binary number string if it contains a single element. This
     * method calls {@link Json5Element#getAsBinaryString()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a binary number string if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public String getAsBinaryString() {
        return getAsSingleElement().getAsBinaryString();
    }

    /**
     * Convenience method to get this array as a octal number string if it contains a single element. This
     * method calls {@link Json5Element#getAsOctalString()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a octal number string if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public String getAsOctalString() {
        return getAsSingleElement().getAsOctalString();
    }

    /**
     * Convenience method to get this array as a hex number string if it contains a single element. This
     * method calls {@link Json5Element#getAsHexString()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a hex number string if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public String getAsHexString() {
        return getAsSingleElement().getAsHexString();
    }

    /**
     * Convenience method to get this array as a {@link String} if it contains a single element. This
     * method calls {@link Json5Element#getAsString()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a String if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public String getAsString() {
        return getAsSingleElement().getAsString();
    }

    /**
     * Convenience method to get this array as a double if it contains a single element. This method
     * calls {@link Json5Element#getAsDouble()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a double if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public double getAsDouble() {
        return getAsSingleElement().getAsDouble();
    }

    /**
     * Convenience method to get this array as a {@link BigDecimal} if it contains a single element.
     * This method calls {@link Json5Element#getAsBigDecimal()} on the element, therefore any of the
     * exceptions declared by that method can occur.
     *
     * @return this element as a {@link BigDecimal} if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public BigDecimal getAsBigDecimal() {
        return getAsSingleElement().getAsBigDecimal();
    }

    /**
     * Convenience method to get this array as a {@link BigInteger} if it contains a single element.
     * This method calls {@link Json5Element#getAsBigInteger()} on the element, therefore any of the
     * exceptions declared by that method can occur.
     *
     * @return this element as a {@link BigInteger} if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public BigInteger getAsBigInteger() {
        return getAsSingleElement().getAsBigInteger();
    }

    /**
     * Convenience method to get this array as a float if it contains a single element. This method
     * calls {@link Json5Element#getAsFloat()} on the element, therefore any of the exceptions declared
     * by that method can occur.
     *
     * @return this element as a float if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public float getAsFloat() {
        return getAsSingleElement().getAsFloat();
    }

    /**
     * Convenience method to get this array as a long if it contains a single element. This method
     * calls {@link Json5Element#getAsLong()} on the element, therefore any of the exceptions declared
     * by that method can occur.
     *
     * @return this element as a long if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public long getAsLong() {
        return getAsSingleElement().getAsLong();
    }

    /**
     * Convenience method to get this array as an integer if it contains a single element. This method
     * calls {@link Json5Element#getAsInt()} on the element, therefore any of the exceptions declared
     * by that method can occur.
     *
     * @return this element as an integer if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public int getAsInt() {
        return getAsSingleElement().getAsInt();
    }

    /**
     * Convenience method to get this array as a primitive byte if it contains a single element. This
     * method calls {@link Json5Element#getAsByte()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a primitive byte if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public byte getAsByte() {
        return getAsSingleElement().getAsByte();
    }

    /**
     * Convenience method to get this array as a primitive short if it contains a single element. This
     * method calls {@link Json5Element#getAsShort()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a primitive short if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public short getAsShort() {
        return getAsSingleElement().getAsShort();
    }

    /**
     * Convenience method to get this array as a boolean if it contains a single element. This method
     * calls {@link Json5Element#getAsBoolean()} on the element, therefore any of the exceptions
     * declared by that method can occur.
     *
     * @return this element as a boolean if it is single element array.
     * @throws IllegalStateException if the array is empty or has more than one element.
     */
    @Override
    public boolean getAsBoolean() {
        return getAsSingleElement().getAsBoolean();
    }

    /**
     * Returns a mutable {@link List} view of this {@code Json5Array}. Changes to the {@code List} are
     * visible in this {@code Json5Array} and the other way around.
     *
     * <p>The {@code List} does not permit {@code null} elements. Unlike {@code Json5Array}'s {@code
     * null} handling, a {@link NullPointerException} is thrown when trying to add {@code null}. Use
     * {@link Json5Null} for Json5 null values.
     *
     * @return mutable {@code List} view
     */
    public List<Json5Element> asList() {
        return new NonNullElementWrapperList<>(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Json5Array that = (Json5Array) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements);
    }
}

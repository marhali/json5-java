/*
 * Copyright (C) 2008 Google Inc.
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

package de.marhali.json5;

import de.marhali.json5.internal.LinkedTreeMap;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A class representing an object type in Json5. An object consists of name-value pairs where names
 * are strings, and values are any other type of {@link Json5Element}. This allows for a creating a
 * tree of Json5Elements. The member elements of this object are maintained in order they were added.
 * This class does not support {@code null} values. If {@code null} is provided as value argument to
 * any of the methods, it is converted to a {@link Json5Null}.
 *
 * <p>{@code Json5Object} does not implement the {@link Map} interface, but a {@code Map} view of it
 * can be obtained with {@link #asMap()}.
 *
 * <p>See the {@link Json5} documentation for details on how to convert {@code Json5Object} and
 * generally any {@code Json5Element} from and to Json5.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @author Marcel Haßlinger
 */
public final class Json5Object extends Json5Element {
    private final LinkedTreeMap<String, Json5Element> members = new LinkedTreeMap<>(false);

    /**
     * Creates a deep copy of this element and all its children.
     */
    @Override
    public Json5Object deepCopy() {
        Json5Object result = new Json5Object();
        for (Map.Entry<String, Json5Element> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().deepCopy());
        }
        result.setComment(comment);
        return result;
    }

    /**
     * Adds a member, which is a name-value pair, to self. The name must be a String, but the value
     * can be an arbitrary {@link Json5Element}, thereby allowing you to build a full tree of
     * Json5Elements rooted at this node.
     *
     * @param property name of the member.
     * @param value the member object.
     */
    public void add(String property, Json5Element value) {
        members.put(property, value == null ? new Json5Null() : value);
    }

    /**
     * Removes the {@code property} from this object.
     *
     * @param property name of the member that should be removed.
     * @return the {@link Json5Element} object that is being removed, or {@code null} if no member with
     *     this name exists.
     */
    public Json5Element remove(String property) {
        return members.remove(property);
    }

    /**
     * Convenience method to add a string member. The specified value is converted to a {@link
     * Json5Primitive} of String.
     *
     * @param property name of the member.
     * @param value the string value associated with the member.
     */
    public void addProperty(String property, String value) {
        add(property, value == null ? new Json5Null() : Json5Primitive.fromString(value));
    }

    /**
     * Convenience method to add a number member. The specified value is converted to a {@link
     * Json5Primitive} of Number.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    public void addProperty(String property, Number value) {
        add(property, value == null ? new Json5Null() : Json5Primitive.fromNumber(value));
    }

    public void addProperty(String property, Number value, int radix) {
        add(property, value == null ? new Json5Null() : Json5Primitive.fromNumber(value, radix));
    }

    /**
     * Convenience method to add a boolean member. The specified value is converted to a {@link
     * Json5Primitive} of Boolean.
     *
     * @param property name of the member.
     * @param value the boolean value associated with the member.
     */
    public void addProperty(String property, Boolean value) {
        add(property, value == null ? new Json5Null() : Json5Primitive.fromBoolean(value));
    }

    /**
     * Convenience method to add a char member. The specified value is converted to a {@link
     * Json5Primitive} of Character.
     *
     * @param property name of the member.
     * @param value the char value associated with the member.
     */
    public void addProperty(String property, Character value) {
        add(property, value == null ? new Json5Null() : Json5Primitive.fromCharacter(value));
    }

    /**
     * Returns a set of members of this object. The set is ordered, and the order is in which the
     * elements were added.
     *
     * @return a set of members of this object.
     */
    public Set<Map.Entry<String, Json5Element>> entrySet() {
        return members.entrySet();
    }

    /**
     * Returns a set of members key values.
     *
     * @return a set of member keys as Strings
     */
    public Set<String> keySet() {
        return members.keySet();
    }

    /**
     * Returns the number of key/value pairs in the object.
     *
     * @return the number of key/value pairs in the object.
     */
    public int size() {
        return members.size();
    }

    /**
     * Returns true if the number of key/value pairs in the object is zero.
     *
     * @return true if the number of key/value pairs in the object is zero.
     */
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Convenience method to check if a member with the specified name is present in this object.
     *
     * @param memberName name of the member that is being checked for presence.
     * @return true if there is a member with the specified name, false otherwise.
     */
    public boolean has(String memberName) {
        return members.containsKey(memberName);
    }

    /**
     * Returns the member with the specified name.
     *
     * @param memberName name of the member that is being requested.
     * @return the member matching the name, or {@code null} if no such member exists.
     */
    public Json5Element get(String memberName) {
        return members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a {@link Json5Primitive}.
     *
     * @param memberName name of the member being requested.
     * @return the {@code Json5Primitive} corresponding to the specified member, or {@code null} if no
     *     member with this name exists.
     * @throws ClassCastException if the member is not of type {@code Json5Primitive}.
     */
    public Json5Primitive getAsJson5Primitive(String memberName) {
        return (Json5Primitive) members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a {@link Json5Array}.
     *
     * @param memberName name of the member being requested.
     * @return the {@code Json5Array} corresponding to the specified member, or {@code null} if no
     *     member with this name exists.
     * @throws ClassCastException if the member is not of type {@code Json5Array}.
     */
    public Json5Array getAsJson5Array(String memberName) {
        return (Json5Array) members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a {@link Json5Object}.
     *
     * @param memberName name of the member being requested.
     * @return the {@code Json5Object} corresponding to the specified member, or {@code null} if no
     *     member with this name exists.
     * @throws ClassCastException if the member is not of type {@code Json5Object}.
     */
    public Json5Object getAsJson5Object(String memberName) {
        return (Json5Object) members.get(memberName);
    }

    /**
     * Returns a mutable {@link Map} view of this {@code Json5Object}. Changes to the {@code Map} are
     * visible in this {@code Json5Object} and the other way around.
     *
     * <p>The {@code Map} does not permit {@code null} keys or values. Unlike {@code Json5Object}'s
     * {@code null} handling, a {@link NullPointerException} is thrown when trying to add {@code
     * null}. Use {@link Json5Null} for Json5 null values.
     *
     * @return mutable {@code Map} view
     */
    public Map<String, Json5Element> asMap() {
        // It is safe to expose the underlying map because it disallows null keys and values
        return members;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Json5Object that = (Json5Object) o;
        return Objects.equals(members, that.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), members);
    }
}

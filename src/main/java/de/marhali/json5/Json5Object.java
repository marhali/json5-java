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

import de.marhali.json5.internal.LinkedTreeMap;

import java.util.Map;
import java.util.Set;

/**
 * A class representing an object type in Json. An object consists of name-value pairs where names
 * are strings, and values are any other type of {@link Json5Element}. This allows for creating a
 * tree of Json5Elements. The member elements of this object are maintained in order they were added.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
@SuppressWarnings("unused")
public final class Json5Object extends Json5Element {
    private final LinkedTreeMap<String, Json5Element> members = new LinkedTreeMap<String, Json5Element>();

    /**
     * Creates a deep copy of this element and all its children
     */
    @Override
    public Json5Object deepCopy() {
        Json5Object result = new Json5Object();
        for (Map.Entry<String, Json5Element> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().deepCopy());
        }
        result.setComment(this.getComment());
        return result;
    }

    @Override
    public Json5Element noCommentCopy() {
        Json5Object result = new Json5Object();
        for (Map.Entry<String, Json5Element> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().noCommentCopy());
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return members.isEmpty();
    }

    /**
     * Adds a member, which is a name-value pair, to self. The name must be a String, but the value
     * can be an arbitrary Json5Element, thereby allowing you to build a full tree of Json5Elements
     * rooted at this node.
     *
     * @param property name of the member.
     * @param value the member object.
     */
    public void add(String property, Json5Element value) {
        members.put(property, value == null ? Json5Null.INSTANCE : value);
        if (value != null && value.hasComment()) {
            setComment(property, value.getComment());
        }
    }

    /**
     * 将本对象的非空注释覆盖合并到目标对象
     * 要求目标对象的同key子节点应与本子节点具备类型一致性
     *
     * @param target 目标对象
     */
    public void mergeCommentTo(Json5Object target) {
        super.copyCommentTo(target);
        for (String key : target.keySet()) {
            Json5Element element = this.get(key);
            if (element != null && !element.hasComment()) {
                continue;
            }
            if (element instanceof Json5Primitive) {
                element.copyCommentTo(target.get(key));
            } else if (element instanceof Json5Array) {
                ((Json5Array) element).mergeCommentTo((Json5Array) target.get(key));
            } else if (element instanceof Json5Object) {
                ((Json5Object) element).mergeCommentTo((Json5Object) target.get(key));
            }
            target.setComment(key, this.getComment(key));
        }
    }

    /**
     * Removes the {@code property} from this {@link Json5Object}.
     *
     * @param property name of the member that should be removed.
     * @return the {@link Json5Element} object that is being removed.
     */
    @SuppressWarnings("UnusedReturnValue")
    public Json5Element remove(String property) {
        return members.remove(property);
    }

    /**
     * Convenience method to add a primitive member. The specified value is converted to a
     * Json5Primitive of String.
     *
     * @param property name of the member.
     * @param value the string value associated with the member.
     */
    public void addProperty(String property, String value) {
        add(property, value == null ? Json5Null.INSTANCE : new Json5String(value));
    }

    /**
     * Convenience method to add a primitive member. The specified value is converted to a
     * Json5Primitive of Number.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    public void addProperty(String property, Number value) {
        add(property, value == null ? Json5Null.INSTANCE : new Json5Number(value));
    }

    /**
     * Convenience method to add a boolean member. The specified value is converted to a
     * Json5Primitive of Boolean.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    public void addProperty(String property, Boolean value) {
        add(property, value == null ? Json5Null.INSTANCE : new Json5Boolean(value));
    }

    /**
     * Convenience method to add a char member. The specified value is converted to a
     * Json5Primitive of Character.
     *
     * @param property name of the member.
     * @param value the number value associated with the member.
     */
    public void addProperty(String property, Character value) {
        add(property, value == null ? Json5Null.INSTANCE : new Json5String(value.toString()));
    }

    //<editor-fold desc="Modified by Ultreon (added support for comments)">

    /**
     * Sets the comment for a member.
     * comment字段不会参与equals 和hashCode
     *
     * @param property name of the member
     * @param comment comment for the member
     */
    public void setComment(String property, String comment) {
        Json5Element element = get(property);
        if (element == null) {
            throw new RuntimeException(
                "Property " + property + " is not defined! You should define a property before set comment.");
        }
        if (element instanceof Json5Null) {
            Json5Null value = new Json5Null();
            value.setComment(comment);
            members.put(property, value);
        } else {
            element.setComment(comment);
        }
    }

    /**
     * Gets the comment for a member.
     *
     * @param property name of the member
     * @return comment for the member
     */
    public String getComment(String property) {
        return get(property).getComment();
    }
    //</editor-fold>

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
     * Convenience method to check if a member with the specified name is present in this object.
     *
     * @param memberName the name of the member that is being checked for presence.
     * @return true if there is a member with the specified name, false otherwise.
     */
    public boolean has(String memberName) {
        return members.containsKey(memberName);
    }

    /**
     * Returns the member with the specified name.
     *
     * @param memberName name of the member that is being requested.
     * @return the member matching the name. Null if no such member exists.
     */
    public Json5Element get(String memberName) {
        return members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a Json5Primitive element.
     *
     * @param memberName the name of the member being requested.
     * @return the Json5Primitive corresponding to the specified member.
     */
    public Json5Primitive getAsJson5Primitive(String memberName) {
        return (Json5Primitive) members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a Json5Array.
     *
     * @param memberName the name of the member being requested.
     * @return the Json5Array corresponding to the specified member.
     */
    public Json5Array getAsJson5Array(String memberName) {
        return (Json5Array) members.get(memberName);
    }

    /**
     * Convenience method to get the specified member as a Json5Object.
     *
     * @param memberName the name of the member being requested.
     * @return the Json5Object corresponding to the specified member.
     */
    public Json5Object getAsJson5Object(String memberName) {
        return (Json5Object) members.get(memberName);
    }

    /**
     * 暂不支持直接转POJO对象,请使用toStandardString然后使用其它反序列化框架处理
     * @see Json5Element#toString(Json5Options)
     * @see Json5Element#toStandardString
     *
     * @param clazz The class of the POJO to convert to.
     * @param <T> The type of the POJO.
     * @return An instance of the specified POJO class, populated with data from this Json5Object.
     */
    public <T> T toPojo(Class<T> clazz) throws Exception {
        throw new Exception("不支持的操作!");
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof Json5Object && ((Json5Object) o).members.equals(members));
    }
}
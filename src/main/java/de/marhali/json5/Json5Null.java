/*
 * Copyright (C) 2008 Google Inc.
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

/**
 * A class representing a Json {@code null} literal value.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public final class Json5Null extends Json5Element {
    /**
     * Singleton for json {@code null} literal
     */
    public static final Json5Null INSTANCE = new Json5Null();

    /**
     * Constructor for internal use only. Use {@link #INSTANCE} instead.
     */
    public Json5Null() { }

    /**
     * Returns the same instance since it is an immutable value
     */
    @Override
    public Json5Null deepCopy() {
        Json5Null json5Null = new Json5Null();
        json5Null.setComment(this.getComment());
        return json5Null;
    }

    @Override
    public Json5Element noCommentCopy() {
        return INSTANCE;
    }

    /**
     * All instances of JsonNull have the same hash code since they are indistinguishable
     */
    @Override
    public int hashCode() {
        return Json5Null.class.hashCode();
    }

    /**
     * All instances of JsonNull are the same
     */
    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Json5Null;
    }
}
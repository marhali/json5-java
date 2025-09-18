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

/**
 * A class representing a Json {@code null} literal value.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 * @author Marcel Haßlinger
 */
public final class Json5Null extends Json5Element {
    public Json5Null() {}

    @Override
    public Json5Element deepCopy() {
        Json5Null copy = new Json5Null();
        copy.setComment(comment);
        return copy;
    }

    @Override
    public String getAsString() {
        return "null";
    }
}

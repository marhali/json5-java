/*
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

/**
 * A class representing a json5 string value.
 *
 * @author Marcel Haßlinger
 */
public final class Json5String extends Json5Primitive {
    public Json5String(String string) {
        super(string);
    }

    @Override
    public Json5Element deepCopy() {
        Json5String o = new Json5String((String) value);
        o.setComment(getComment());
        return o;
    }

    @Override
    public Json5Element noCommentCopy() {
        return new Json5String((String) value);
    }

    @Override
    public boolean isEmpty() {
        return ((String) this.value).isEmpty();
    }
}

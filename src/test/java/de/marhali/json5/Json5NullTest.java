/*
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

import de.marhali.json5.fixtures.ToStringFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class Json5NullTest {
    @Test
    @DisplayName("deepCopy(): it should just copy comment")
    void test_deepCopy() {
        Json5Null source = new Json5Null();
        String sourceComment = "my comment";
        source.setComment(sourceComment);

        Json5Element copy = source.deepCopy();
        String newComment = "new comment";
        source.setComment(newComment);

        assertTrue(copy.isJson5Null());
        assertEquals(sourceComment, copy.getComment());
        assertEquals(newComment, source.getComment());
    }

    @Test
    @DisplayName("getAsString(): it should return null value")
    void test_getAsString() {
        var element = new Json5Null();
        assertEquals("null", element.getAsString());
    }

    @Test
    void test_toString() {
        var element = new Json5Null();
        assertEquals("null", element.toString(ToStringFixtures.OPTIONS));
    }
}

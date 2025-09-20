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

package de.marhali.json5.e2e.failures;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import de.marhali.json5.config.DuplicateKeyStrategy;
import de.marhali.json5.e2e.TestResourceHelper;
import de.marhali.json5.exception.Json5Exception;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class DuplicateObjectKeyTest {
    @Test
    @DisplayName("Parse: duplicate key on object throws with DuplicateStrategy.UNIQUE")
    void disallowDuplicateObjectKey() {
        var json5 = Json5.builder(builder -> builder.duplicateKeyStrategy(DuplicateKeyStrategy.UNIQUE).build());

        var ex = assertThrows(Json5Exception.class, () -> json5.parse(TestResourceHelper.getTestResourceContent("e2e/failures/duplicate-object-key.json5")));

        assertEquals("Duplicate key \"alpha\" at index 37 [character 8 in line 3]", ex.getMessage());
    }

    @Test
    @DisplayName("Parse: duplicate key on object, but last one wins with DuplicateStrategy.LAST_WINS")
    void duplicateObjectKeyLastWins() {
        var json5 = Json5.builder(builder -> builder.duplicateKeyStrategy(DuplicateKeyStrategy.LAST_WINS).build());

        Json5Element element = json5.parse(TestResourceHelper.getTestResource("e2e/failures/duplicate-object-key.json5"));

        assertTrue(element.isJson5Object());
        var object = element.getAsJson5Object();

        assertEquals(3, object.size());
        assertEquals("secondAlphaValue", object.get("alpha").getAsString());
        assertEquals("bravoValue", object.get("bravo").getAsString());
        assertEquals("secondCharlieValue", object.get("charlie").getAsString());
    }

    @Test
    @DisplayName("Parse: duplicate key on object, but all entries are converted to array with DuplicateStrategy.DUPLICATE")
    void duplicateObjectKeyAsArray() {
        var json5 = Json5.builder(builder -> builder.duplicateKeyStrategy(DuplicateKeyStrategy.DUPLICATE).build());

        Json5Element element = json5.parse(TestResourceHelper.getTestResource("e2e/failures/duplicate-object-key.json5"));

        assertTrue(element.isJson5Object());
        var object = element.getAsJson5Object();

        assertEquals(3, object.size());
        assertTrue(object.get("alpha").isJson5Array());
        assertEquals(Json5Primitive.fromString("firstAlphaValue"), object.get("alpha").getAsJson5Array().get(0));
        assertEquals(Json5Primitive.fromString("secondAlphaValue"), object.get("alpha").getAsJson5Array().get(1));
        assertEquals("bravoValue", object.get("bravo").getAsString());
        assertEquals(Json5Primitive.fromString("firstCharlieValue"), object.get("charlie").getAsJson5Array().get(0));
        assertEquals(Json5Primitive.fromString("secondCharlieValue"), object.get("charlie").getAsJson5Array().get(1));
    }
}

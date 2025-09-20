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
import de.marhali.json5.config.Json5Options;
import de.marhali.json5.e2e.TestResourceHelper;
import de.marhali.json5.exception.Json5Exception;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcel Haßlinger
 */
public class DisallowCDigitSeparatorTest {
    @Test
    @DisplayName("Parse: disallowed C-style digit separators throws exception")
    void disallowCCDigitSeparator() {
        var json5 = Json5.builder(Json5Options.Builder::build);

        var ex = assertThrows(Json5Exception.class, () -> json5.parse(TestResourceHelper.getTestResourceContent("e2e/failures/disallow-c-digit-separator.json5")));

        assertEquals("C-style digit separators are not allowed at index 28 [character 1 in line 3]", ex.getMessage());
    }
}

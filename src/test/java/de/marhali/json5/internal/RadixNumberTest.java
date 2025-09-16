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

package de.marhali.json5.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class RadixNumberTest {

    @Test
    void testNumberGetter() {
        assertEquals(187, new RadixNumber(187, 10).getNumber());
    }

    @Test
    void testRadixGetter() {
        assertEquals(10, new RadixNumber(187, 10).getRadix());
    }
}

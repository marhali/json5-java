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

package de.marhali.json5.fixtures;

import de.marhali.json5.config.DigitSeparatorStrategy;
import de.marhali.json5.config.DuplicateKeyStrategy;
import de.marhali.json5.config.Json5Options;

/**
 * @author Marcel Haßlinger
 */
public class ToStringFixtures {
    /**
     * Options to use for testing {@link de.marhali.json5.Json5Element#toString(Json5Options)} methods.
     */
    public static Json5Options OPTIONS = Json5Options.builder()
        .allowNaN()
        .allowInfinity()
        .allowInvalidSurrogates()
        .parseComments()
        .writeComments()
        .trailingComma()
        .quoteSingle()
        .digitSeparatorStrategy(DigitSeparatorStrategy.NONE)
        .duplicateKeyStrategy(DuplicateKeyStrategy.UNIQUE)
        .prettyPrinting()
        .build();
}

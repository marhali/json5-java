/*
 * MIT License
 *
 * Copyright (C) 2021 SyntaxError404
 * Copyright (C) 2025 Marcel Haßlinger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.marhali.json5.config;

/**
 * An enum containing all supported behaviors for duplicate keys
 *
 * @author SyntaxError404
 * @author Marcel Haßlinger
 */
public enum DuplicateKeyStrategy {

    /**
     * Throws an {@link de.marhali.json5.exception.Json5Exception exception} when a key
     * is encountered multiple times within the same object
     */
    UNIQUE,

    /**
     * Only the last encountered value is significant,
     * all previous occurrences are silently discarded
     */
    LAST_WINS,

    /**
     * Wraps duplicate values inside an {@link de.marhali.json5.Json5Array array},
     * effectively treating them as if they were declared as one
     */
    DUPLICATE

}

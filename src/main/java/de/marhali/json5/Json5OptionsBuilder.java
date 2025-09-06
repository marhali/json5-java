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
 * Options builder to configure behaviour of json5 parsing and serialization.
 *
 * @author Marcel Haßlinger
 * @see Json5Options
 */
public class Json5OptionsBuilder {

    private boolean allowInvalidSurrogates = false;
    private boolean quoteSingle = false;
    private boolean trailingComma = false;

    //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
    private boolean quoteless = false;
    //</editor-fold>

    private int indentFactor = 0;

    private boolean readComments = false;

    private boolean writeComments = true;

    /**
     * Constructs a new builder instance.
     */
    public Json5OptionsBuilder() {}

    /**
     * @see Json5Options#isAllowInvalidSurrogates()
     * @return Current builder instance
     */
    public Json5OptionsBuilder allowInvalidSurrogate() {
        this.allowInvalidSurrogates = true;
        return this;
    }

    /**
     * @see Json5Options#isQuoteSingle()
     * @return Current builder instance
     */
    public Json5OptionsBuilder quoteSingle() {
        this.quoteSingle = true;
        return this;
    }

    /**
     * @see Json5Options#isTrailingComma()
     * @return Current builder instance
     */
    public Json5OptionsBuilder trailingComma() {
        this.trailingComma = true;
        return this;
    }

    /**
     * The indentation factor enables pretty-printing and defines
     * how many spaces ( {@code ' '}) should be placed before each key/value pair.
     * A factor of {@code < 1} disables pretty-printing and discards
     * any optional whitespace characters.
     * @see Json5Options#getIndentFactor()
     * @param indentFactor Indent factor to apply
     * @return Current builder instance
     */
    public Json5OptionsBuilder indentFactor(int indentFactor) {
        this.indentFactor = indentFactor;
        return this;
    }

    /**
     * Configures to output Json5 that fits in a page for pretty printing. This option only affects Json serialization.
     * Applies an indent factor of 2.
     * @see #indentFactor(int)
     * @return Current builder instance
     */
    public Json5OptionsBuilder prettyPrinting() {
        this.indentFactor = 2;
        return this;
    }

    //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
    /**
     * Configures to output {@link Json5Object} keys without quotes. This option only affects Json serialization.
     * This option has no effect if the key starts or ends with a digit, or if the key contains non-alphanumeric characters.
     *
     * @return Current builder instance
     */
    public Json5OptionsBuilder quoteless() {
        this.quoteless = true;
        return this;
    }
    //</editor-fold>

    /**
     * Configures to read comments from the source. This option only affects Json parsing.
     * @return Current builder instance
     */
    public Json5OptionsBuilder readComments() {
        this.readComments = true;
        return this;
    }

    /**
     * 设置json5Options序列化时不写注释
     */
    public Json5OptionsBuilder notWriteComments() {
        this.writeComments = false;
        return this;
    }
    /**
     * @return Configured {@link Json5Options}
     */
    public Json5Options build() {
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        return new Json5Options(allowInvalidSurrogates, quoteSingle, trailingComma, indentFactor, quoteless,
            readComments,writeComments);
        //</editor-fold>
    }
}

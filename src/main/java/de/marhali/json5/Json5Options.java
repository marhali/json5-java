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
 * Configuration options for Json5 parsing and serialization.
 *
 * @author Marcel Haßlinger
 */
public class Json5Options {

    /**
     * Default configuration options. Allow invalid surrogate, disabled pretty-printing.
     */
    public static final Json5Options DEFAULT = new Json5OptionsBuilder()
            .allowInvalidSurrogate()
            .trailingComma()
            .prettyPrinting()
            .build();

    /**
     * @return Builder pattern to configure json5 behaviour.
     */
    public static Json5OptionsBuilder builder() {
        return new Json5OptionsBuilder();
    }

    /**
     * Whether invalid unicode surrogate pairs should be allowed
     */
    private final boolean allowInvalidSurrogates;

    /**
     * Whether strings should be single-quoted ({@code '}) instead of double-quoted ({@code "}).
     * This also includes all member names of {@link Json5Object}.
     */
    private final boolean quoteSingle;

    /**
     * Whether all Json5 values should be marked with a trailing comma ({@code ,}) or only where it is mandatory.
     */
    private final boolean trailingComma;

    /**
     * Defines how many spaces ({@code ' '}) should be placed before each key/value pair.
     * A factor of {@code < 1} disables pretty-printing and discards any optional whitespace characters.
     */
    private final int indentFactor;

    //<editor-fold desc="Modified by Ultreon (added quoteless parameter)">
    /**
     * Whether keys of {@link Json5Object} should be without quotes.
     * This is unless starting or ending with a digit.
     */
    private final boolean quoteless;

    /**
     * parse时是否读取注释
     */
    private final boolean readComments;

    /**
     * write时是否写注释
     */
    private final boolean writeComments;

    //</editor-fold>
    public Json5Options(boolean allowInvalidSurrogates, boolean quoteSingle, boolean trailingComma, int indentFactor, boolean quoteless, boolean readComments,
        boolean writeComments) {
        this.allowInvalidSurrogates = allowInvalidSurrogates;
        this.quoteSingle = quoteSingle;
        this.trailingComma = trailingComma;
        this.indentFactor = Math.max(0, indentFactor);
        this.quoteless = quoteless;
        this.readComments = readComments;
        this.writeComments = writeComments;
    }

    public Json5Options(boolean allowInvalidSurrogates, boolean quoteSingle, boolean trailingComma, int indentFactor, boolean quoteless) {
        this.allowInvalidSurrogates = allowInvalidSurrogates;
        this.quoteSingle = quoteSingle;
        this.trailingComma = trailingComma;
        this.indentFactor = Math.max(0, indentFactor);
        this.quoteless = quoteless;
        this.readComments = false;
        this.writeComments = true;
    }

    public boolean isAllowInvalidSurrogates() {
        return allowInvalidSurrogates;
    }

    public boolean isQuoteSingle() {
        return quoteSingle;
    }

    public boolean isWriteComments() {
        return writeComments;
    }

    public boolean isTrailingComma() {
        return trailingComma;
    }

    public int getIndentFactor() {
        return indentFactor;
    }

    public boolean isReadComments() {
        return readComments;
    }

    //<editor-fold desc="Modified by Ultreon (added quoteless getter)">
    public boolean isQuoteless() {
        return quoteless;
    }
    //</editor-fold>
}
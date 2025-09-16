/*
 * MIT License
 *
 * Copyright (C) 2021 SyntaxError404
 * Copyright (C) 2022 - 2025 Marcel Haßlinger
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

import de.marhali.json5.Json5Array;
import de.marhali.json5.Json5Object;

import java.util.Objects;

/**
 * Definition of all configuration options for parsing and writing Json5 data.
 *
 * @author SyntaxError404
 * @author Marcel Haßlinger
 */
public final class Json5Options {

    /**
     * Whether instants should be stringifyed as unix timestamps.
     * If this is {@code false}, instants will be stringifyed as strings
     * (according to <a href="https://datatracker.ietf.org/doc/html/rfc3339#section-5.6">RFC 3339, Section 5.6</a>).
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final boolean stringifyUnixInstants;

    /**
     * Whether stringifying should only yield ASCII strings.
     * All non-ASCII characters will be converted to their
     * Unicode escape sequence (<code>&#92;uXXXX</code>).
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final boolean stringifyAscii;

    /**
     * Whether {@code NaN} should be allowed as a number
     */
    private final boolean allowNaN;

    /**
     * Whether {@code Infinity} should be allowed as a number.
     * This applies to both {@code +Infinity} and {@code -Infinity}
     */
    private final boolean allowInfinity;

    /**
     * Whether invalid unicode surrogate pairs should be allowed
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean allowInvalidSurrogates;

    /**
     * Whether strings should be single-quoted ({@code '}) instead of double-quoted ({@code "}).
     * This also includes a {@link Json5Object JSON5Object's} member names
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final boolean quoteSingle;

    /**
     * Whether member names of {@link Json5Object Json5Object's} should be quoteless.
     * E.g. <code>{ enabled: true }</code> instead of <code>{ "enabled": true }</code>.
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final boolean quoteless;

    /**
     * Whether binary literals ({@code 0b10101...}) should be allowed
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean allowBinaryLiterals;

    /**
     * Whether octal literals ({@code 0o567...}) should be allowed
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean allowOctalLiterals;

    /**
     * Whether hexadecimal floating-point literals (e.g. {@code 0xA.BCp+12}) should be allowed
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean allowHexFloatingLiterals;

    /**
     * Whether 32-bit unicode escape sequences ({@code \U00123456}) should be allowed
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean allowLongUnicodeEscapes;

    /**
     * Specifies whether trailing data should be allowed.<br>
     * If {@code false}, parsing the following will produce an error
     * due to the trailing {@code abc}:
     *
     * <pre><code>{ }abc</code></pre>
     *
     * If {@code true}, however, this will be interpreted as an empty
     * {@link Json5Object} and any trailing will be ignored.
     * <p>
     * Whitespace never counts as trailing data.
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean allowTrailingData;

    /**
     * Specifies whether comments on {@link de.marhali.json5.Json5Element Json5Element's} should be parsed.
     * If {@code false}, no comments will be parsed.
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final boolean parseComments;

    /**
     * Specifies whether comments on {@link de.marhali.json5.Json5Element Json5Element's} should be written.
     * If {@code false}, no set comments will be written.
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final boolean writeComments;

    /**
     * Specifies whether to apply trailing commas whenever possible or not.
     * If {@code false}, commas are only written when necessary.
     * <pre>
     * {@code
     * // trailingComma: false
     * {
     *   "firstKey": "myValue",
     *   "secondKey": "myValue" // <-- no comma
     * }
     *
     * // trailingComma: true
     * {
     *   "firstKey": "myValue",
     *   "secondKey": "myValue", // <-- trailing comma
     * }
     * }
     * </pre>
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final boolean trailingComma;

    /**
     * Specifies the behaviour for digit separator's on numbers.
     * <p>
     * <i>This option applies to both {@link de.marhali.json5.stream.Json5Parser parsing} and {@link de.marhali.json5.stream.Json5Writer writing}.</i>
     */
    private final DigitSeparatorStrategy digitSeparatorStrategy;

    /**
     * Specifies the behaviour when the same key is encountered multiple times within the same {@link Json5Object}
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Parser parser}-only option</i>
     */
    private final DuplicateKeyStrategy duplicateBehaviour;


    /**
     * Defines the amount of whitespace's to use for the indentation of {@link Json5Object}'s or {@link Json5Array}'s.
     * A factor of {@code < 1} disables pretty-printing and discards any optional whitespace characters.
     * <p>
     * <i>This is a {@link de.marhali.json5.stream.Json5Writer writer}-only option</i>
     */
    private final int indentFactor;

    /**
     * Configure options using the builder pattern.
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Recommended default configuration options.
     * <p>
     * Defaults:
     * <ul>
     *     <li>allowNaN: <code>true</code></li>
     *     <li>allowInfinity: <code>true</code></li>
     *     <li>allowInvalidSurrogates: <code>true</code></li>
     *     <li>parseComments: <code>true</code></li>
     *     <li>writeComments: <code>true</code></li>
     *     <li>trailingComma: <code>true</code></li>
     *     <li>digitSeparatorStrategy: <code>NONE</code></li>
     *     <li>duplicateKeyStrategy: <code>UNIQUE</code></li>
     *     <li>prettyPrinting: <code>true</code></li>
     * </ul>
     */
    public static Json5Options DEFAULT = builder()
            .allowNaN()
            .allowInfinity()
            .allowInvalidSurrogates()
            .parseComments()
            .writeComments()
            .trailingComma()
            .digitSeparatorStrategy(DigitSeparatorStrategy.NONE)
            .duplicateKeyStrategy(DuplicateKeyStrategy.UNIQUE)
            .prettyPrinting()
            .build();

    private Json5Options(Builder builder) {
        this.stringifyUnixInstants = builder.stringifyUnixInstants;
        this.stringifyAscii = builder.stringifyAscii;
        this.allowNaN = builder.allowNaN;
        this.allowInfinity = builder.allowInfinity;
        this.allowInvalidSurrogates = builder.allowInvalidSurrogates;
        this.quoteSingle = builder.quoteSingle;
        this.quoteless = builder.quoteless;
        this.allowBinaryLiterals = builder.allowBinaryLiterals;
        this.allowOctalLiterals = builder.allowOctalLiterals;
        this.allowHexFloatingLiterals = builder.allowHexFloatingLiterals;
        this.allowLongUnicodeEscapes = builder.allowLongUnicodeEscapes;
        this.allowTrailingData = builder.allowTrailingData;
        this.parseComments = builder.parseComments;
        this.writeComments = builder.writeComments;
        this.trailingComma = builder.trailingComma;
        this.digitSeparatorStrategy = builder.digitSeparatorStrategy;
        this.duplicateBehaviour = builder.duplicateKeyStrategy;this.indentFactor = builder.indentFactor;
    }

    public boolean isStringifyUnixInstants() {
        return stringifyUnixInstants;
    }

    public boolean isStringifyAscii() {
        return stringifyAscii;
    }

    public boolean isAllowNaN() {
        return allowNaN;
    }

    public boolean isAllowInfinity() {
        return allowInfinity;
    }

    public boolean isAllowInvalidSurrogates() {
        return allowInvalidSurrogates;
    }

    public boolean isQuoteSingle() {
        return quoteSingle;
    }

    public boolean isQuoteless() {
        return quoteless;
    }

    public boolean isAllowBinaryLiterals() {
        return allowBinaryLiterals;
    }

    public boolean isAllowOctalLiterals() {
        return allowOctalLiterals;
    }

    public boolean isAllowHexFloatingLiterals() {
        return allowHexFloatingLiterals;
    }

    public boolean isAllowLongUnicodeEscapes() {
        return allowLongUnicodeEscapes;
    }

    public boolean isAllowTrailingData() {
        return allowTrailingData;
    }

    public boolean isWriteComments() {
        return writeComments;
    }

    public boolean isParseComments() {
        return parseComments;
    }

    public boolean isTrailingComma() {
        return trailingComma;
    }

    public DigitSeparatorStrategy getDigitSeparatorStrategy() {
        return digitSeparatorStrategy;
    }

    public DuplicateKeyStrategy getDuplicateBehaviour() {
        return duplicateBehaviour;
    }

    public int getIndentFactor() {
        return indentFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Json5Options that = (Json5Options) o;
        return stringifyUnixInstants == that.stringifyUnixInstants && stringifyAscii == that.stringifyAscii && allowNaN == that.allowNaN && allowInfinity == that.allowInfinity && allowInvalidSurrogates == that.allowInvalidSurrogates && quoteSingle == that.quoteSingle && quoteless == that.quoteless && allowBinaryLiterals == that.allowBinaryLiterals && allowOctalLiterals == that.allowOctalLiterals && allowHexFloatingLiterals == that.allowHexFloatingLiterals && allowLongUnicodeEscapes == that.allowLongUnicodeEscapes && allowTrailingData == that.allowTrailingData && parseComments == that.parseComments && writeComments == that.writeComments && trailingComma == that.trailingComma && indentFactor == that.indentFactor && digitSeparatorStrategy == that.digitSeparatorStrategy && duplicateBehaviour == that.duplicateBehaviour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringifyUnixInstants, stringifyAscii, allowNaN, allowInfinity, allowInvalidSurrogates, quoteSingle, quoteless, allowBinaryLiterals, allowOctalLiterals, allowHexFloatingLiterals, allowLongUnicodeEscapes, allowTrailingData, parseComments, writeComments, trailingComma, digitSeparatorStrategy, duplicateBehaviour, indentFactor);
    }

    @Override
    public String toString() {
        return "Json5Options{" +
                "stringifyUnixInstants=" + stringifyUnixInstants +
                ", stringifyAscii=" + stringifyAscii +
                ", allowNaN=" + allowNaN +
                ", allowInfinity=" + allowInfinity +
                ", allowInvalidSurrogates=" + allowInvalidSurrogates +
                ", quoteSingle=" + quoteSingle +
                ", quoteless=" + quoteless +
                ", allowBinaryLiterals=" + allowBinaryLiterals +
                ", allowOctalLiterals=" + allowOctalLiterals +
                ", allowHexFloatingLiterals=" + allowHexFloatingLiterals +
                ", allowLongUnicodeEscapes=" + allowLongUnicodeEscapes +
                ", allowTrailingData=" + allowTrailingData +
                ", parseComments=" + parseComments +
                ", writeComments=" + writeComments +
                ", trailingComma=" + trailingComma +
                ", digitSeparatorStrategy=" + digitSeparatorStrategy +
                ", duplicateBehaviour=" + duplicateBehaviour +
                ", indentFactor=" + indentFactor +
                '}';
    }

    public static final class Builder {
        private boolean stringifyUnixInstants = false;
        private boolean stringifyAscii = false;
        private boolean allowNaN = false;
        private boolean allowInfinity = false;
        private boolean allowInvalidSurrogates = false;
        private boolean quoteSingle = false;
        private boolean quoteless = false;
        private boolean allowBinaryLiterals = false;
        private boolean allowOctalLiterals = false;
        private boolean allowHexFloatingLiterals = false;
        private boolean allowLongUnicodeEscapes = false;
        private boolean allowTrailingData =  false;
        private boolean parseComments = false;
        private boolean writeComments = false;
        private boolean trailingComma = false;
        private DigitSeparatorStrategy digitSeparatorStrategy = DigitSeparatorStrategy.NONE;
        private DuplicateKeyStrategy duplicateKeyStrategy = DuplicateKeyStrategy.UNIQUE;
        private int indentFactor = 0;

        private Builder() {}

        /**
         * @return built {@link Json5Options}
         */
        public Json5Options build() {
            return new Json5Options(this);
        }

        /**
         * @see Json5Options#stringifyUnixInstants
         * @return builder
         */
        public Builder stringifyUnixInstants() {
            this.stringifyUnixInstants = true;
            return this;
        }

        /**
         * @see Json5Options#stringifyAscii
         * @return builder
         */
        public Builder stringifyAscii() {
            this.stringifyAscii = true;
            return this;
        }

        /**
         * @see Json5Options#allowNaN
         * @return builder
         */
        public Builder allowNaN() {
            this.allowNaN = true;
            return this;
        }

        /**
         * @see Json5Options#allowInfinity
         * @return builder
         */
        public Builder allowInfinity() {
            this.allowInfinity = true;
            return this;
        }

        /**
         * @see Json5Options#allowInvalidSurrogates
         * @return builder
         */
        public Builder allowInvalidSurrogates() {
            this.allowInvalidSurrogates = true;
            return this;
        }

        /**
         * @see Json5Options#quoteSingle
         * @return builder
         */
        public Builder quoteSingle() {
            this.quoteSingle = true;
            return this;
        }

        /**
         * @see Json5Options#quoteless
         * @return builder
         */
        public Builder quoteless() {
            this.quoteless = true;
            return this;
        }

        /**
         * @see Json5Options#allowBinaryLiterals
         * @return builder
         */
        public Builder allowBinaryLiterals() {
            this.allowBinaryLiterals = true;
            return this;
        }

        /**
         * @see Json5Options#allowOctalLiterals
         * @return builder
         */
        public Builder allowOctalLiterals() {
            this.allowOctalLiterals = true;
            return this;
        }

        /**
         * @see Json5Options#allowHexFloatingLiterals
         * @return builder
         */
        public Builder allowHexFloatingLiterals() {
            this.allowHexFloatingLiterals = true;
            return this;
        }

        /**
         * @see Json5Options#allowLongUnicodeEscapes
         * @return builder
         */
        public Builder allowLongUnicodeEscapes() {
            this.allowLongUnicodeEscapes = true;
            return this;
        }

        /**
         * @see Json5Options#allowTrailingData
         * @return builder
         */
        public Builder allowTrailingData() {
            this.allowTrailingData = true;
            return this;
        }

        /**
         * @see Json5Options#parseComments
         * @return builder
         */
        public Builder parseComments() {
            this.parseComments = true;
            return this;
        }

        /**
         * @see Json5Options#writeComments
         * @return builder
         */
        public Builder writeComments() {
            this.writeComments = true;
            return this;
        }

        /**
         * @see Json5Options#trailingComma
         * @return builder
         */
        public Builder trailingComma() {
            this.trailingComma = true;
            return this;
        }

        /**
         * @see Json5Options#digitSeparatorStrategy
         * @return builder
         */
        public Builder digitSeparatorStrategy(DigitSeparatorStrategy digitSeparatorStrategy) {
            this.digitSeparatorStrategy = digitSeparatorStrategy;
            return this;
        }

        /**
         * @see Json5Options#duplicateBehaviour
         * @return builder
         */
        public Builder duplicateKeyStrategy(DuplicateKeyStrategy duplicateKeyStrategy) {
            this.duplicateKeyStrategy = duplicateKeyStrategy;
            return this;
        }

        /**
         * @see Json5Options#indentFactor
         * @return builder
         */
        public Builder indentFactor(int indentFactor) {
            this.indentFactor = indentFactor;
            return this;
        }

        /**
         * Configures pretty printing using 2 whitespaces for serialization (writing).
         * Shorthand for {@code indentFactor(2)}.
         * @see Json5Options#indentFactor
         * @see #indentFactor(int)
         * @return builder
         */
        public Builder prettyPrinting() {
            return indentFactor(2);
        }
    }
}

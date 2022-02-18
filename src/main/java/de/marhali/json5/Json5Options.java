/*
 * MIT License
 *
 * Copyright (c) 2021 SyntaxError404
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

package de.marhali.json5;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Json5 configuration options to customize the parsing and serialization behaviour.
 *
 * @author SyntaxError404
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@Builder(toBuilder = true)
public class Json5Options {

    /**
     * -- GETTER --
     * Returns the default options for parsing and stringifying
     *
     * @return the default options
     * @since 1.1.0
     *
     * -- SETTER --
     * Sets the default options for parsing and stringifying.
     * Must not be {@code null}
     *
     * @param defaultOptions the new default options
     * @since 1.1.0
     */
    @Getter
    @Setter
    @NonNull
    private static Json5Options defaultOptions = builder().build();

    /**
     * Whether instants should be parsed as such.
     * If this is {@code false}, {@link #isParseStringInstants()} and {@link #isParseUnixInstants()}
     * are ignored
     * <p>
     * Default: {@code true}
     * <p>
     * <i>This is a parser-only option</i>
     *
     * @param parseInstants a boolean
     *
     * @return whether instants should be parsed
     * @since 1.1.0
     */
    @Builder.Default
    boolean parseInstants = true;
    /**
     * Whether string instants (according to
     * <a href="https://datatracker.ietf.org/doc/html/rfc3339#section-5.6">RFC 3339, Section 5.6</a>)
     * should be parsed as such.
     * Ignored if {@link #isParseInstants()} is {@code false}
     * <p>
     * Default: {@code true}
     * <p>
     * <i>This is a parser-only option</i>
     *
     * @param parseStringInstants a boolean
     *
     * @return whether string instants should be parsed
     * @since 1.1.0
     */
    @Builder.Default
    boolean parseStringInstants = true;
    /**
     * Whether unix instants (integers) should be parsed as such.
     * Ignored if {@link #isParseInstants()} is {@code false}
     * <p>
     * Default: {@code true}
     * <p>
     * <i>This is a parser-only option</i>
     *
     * @param parseUnixInstants a boolean
     *
     * @return whether unix instants should be parsed
     * @since 1.1.0
     */
    @Builder.Default
    boolean parseUnixInstants = true;

    /**
     * Whether instants should be stringifyed as unix timestamps.
     * If this is {@code false}, instants will be stringifyed as strings
     * (according to <a href="https://datatracker.ietf.org/doc/html/rfc3339#section-5.6">RFC 3339, Section 5.6</a>).
     * <p>
     * Default: {@code false}
     * <p>
     * <i>This is a serialization-only option</i>
     *
     * @param stringifyUnixInstants a boolean
     *
     * @return whether instants should be stringifyed as unix timestamps
     * @since 1.1.0
     */
    @Builder.Default
    boolean stringifyUnixInstants = false;

    /**
     * Whether {@code NaN} should be allowed as a number
     * <p>
     * Default: {@code true}
     *
     * @param allowNaN a boolean
     *
     * @return whether {@code NaN} should be allowed
     * @since 1.1.0
     */
    @Builder.Default
    boolean allowNaN = true;

    /**
     * Whether {@code Infinity} should be allowed as a number.
     * This applies to both {@code +Infinity} and {@code -Infinity}
     * <p>
     * Default: {@code true}
     *
     * @param allowInfinity a boolean
     *
     * @return whether {@code Infinity} should be allowed
     * @since 1.1.0
     */
    @Builder.Default
    boolean allowInfinity = true;

    /**
     * Whether invalid unicode surrogate pairs should be allowed
     * <p>
     * Default: {@code true}
     * <p>
     * <i>This is a parser-only option</i>
     *
     * @param allowInvalidSurrogates a boolean
     *
     * @return whether invalid unicode surrogate pairs should be allowed
     * @since 1.1.0
     */
    @Builder.Default
    boolean allowInvalidSurrogates = true;

    /**
     * Whether strings should be single-quoted ({@code '}) instead of double-quoted ({@code "}).
     * This also includes all member names of {@link Json5Object}.
     * <p>
     * Default: {@code false}
     * <p>
     * <i>This is a serialization-only option</i>
     *
     * @param quoteSingle a boolean
     *
     * @return whether strings should be single-quoted
     * @since 1.1.0
     */
    @Builder.Default
    boolean quoteSingle = false;

}
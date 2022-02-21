/*
 * MIT License
 *
 * Copyright (C) 2021 SyntaxError404
 * Copyright (C) 2022 Marcel Haßlinger
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

package de.marhali.json5.stream;

import de.marhali.json5.*;
import de.marhali.json5.exception.Json5Exception;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * This is a lexer to convert the provided data into tokens according to the json5 specification.
 * The resulting tokens can then be used in an appropriate parser to construct
 * {@link Json5Object}'s and {@link Json5Array}'s.
 *
 * @author Marcel Haßlinger
 * @author SyntaxError404
 * @see <a href="https://spec.json5.org/">Json5 Standard</a>.
 */
public class Json5Lexer {

    private static final Pattern PATTERN_BOOLEAN = Pattern.compile(
            "true|false"
    );

    private static final Pattern PATTERN_NUMBER_FLOAT = Pattern.compile(
            "[+-]?((0|[1-9]\\d*)(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?"
    );
    private static final Pattern PATTERN_NUMBER_INTEGER = Pattern.compile(
            "[+-]?(0|[1-9]\\d*)"
    );
    private static final Pattern PATTERN_NUMBER_HEX = Pattern.compile(
            "[+-]?0[xX][0-9a-fA-F]+"
    );
    private static final Pattern PATTERN_NUMBER_SPECIAL = Pattern.compile(
            "[+-]?(Infinity|NaN)"
    );

    private final Reader reader;
    private final Json5Options options;

    /**
     * whether the end of the file has been reached
     */
    private boolean eof;

    /**
     * whether the current character should be re-read
     */
    private boolean back;

    /**
     * the absolute position in the string
     */
    private long index;
    /**
     * the relative position in the line
     */
    private long character;
    /**
     * the line number
     */
    private long line;

    /**
     * the previous character
     */
    private char previous;
    /**
     * the current character
     */
    private char current;

    /**
     * Constructs a new lexer from a specific {@link Reader}.
     * @param reader  a reader.
     * @param options the options for lexing.
     * @apiNote The reader must be closed after operation ({@link Reader#close()})!
     */
    public Json5Lexer(Reader reader, Json5Options options) {
        this.reader = Objects.requireNonNull(reader).markSupported() ? reader : new BufferedReader(reader);
        this.options = Objects.requireNonNull(options);

        eof = false;
        back = false;

        index = -1;
        character = 0;
        line = 1;

        previous = 0;
        current = 0;
    }

    private boolean more() {
        if (back || eof)
            return back && !eof;

        return peek() > 0;
    }

    /**
     * Forces the parser to re-read the last character
     */
    public void back() {
        back = true;
    }

    private char peek() {
        if (eof)
            return 0;

        int c;

        try {
            reader.mark(1);

            c = reader.read();

            reader.reset();
        } catch (Exception e) {
            throw syntaxError("Could not peek from source", e);
        }

        return c == -1 ? 0 : (char) c;
    }

    private char next() {
        if (back) {
            back = false;
            return current;
        }

        int c;

        try {
            c = reader.read();
        } catch (Exception e) {
            throw syntaxError("Could not read from source", e);
        }

        if (c < 0) {
            eof = true;
            return 0;
        }

        previous = current;
        current = (char) c;

        index++;

        if (isLineTerminator(current) && (current != '\n' || (current == '\n' && previous != '\r'))) {
            line++;
            character = 0;
        } else character++;

        return current;
    }

    // https://262.ecma-international.org/5.1/#sec-7.3
    private boolean isLineTerminator(char c) {
        switch (c) {
            case '\n':
            case '\r':
            case 0x2028:
            case 0x2029:
                return true;
            default:
                return false;
        }
    }

    // https://spec.json5.org/#white-space
    private boolean isWhitespace(char c) {
        switch (c) {
            case '\t':
            case '\n':
            case 0x0B: // Vertical Tab
            case '\f':
            case '\r':
            case ' ':
            case 0xA0: // No-break space
            case 0x2028: // Line separator
            case 0x2029: // Paragraph separator
            case 0xFEFF: // Byte Order Mark
                return true;
            default:
                // Unicode category "Zs" (space separators)
                if (Character.getType(c) == Character.SPACE_SEPARATOR)
                    return true;

                return false;
        }
    }

    // https://262.ecma-international.org/5.1/#sec-9.3.1
    private boolean isDecimalDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void nextMultiLineComment() {
        while (true) {
            char n = next();

            if (n == '*' && peek() == '/') {
                next();
                return;
            }
        }
    }

    private void nextSingleLineComment() {
        while (true) {
            char n = next();

            if (isLineTerminator(n) || n == 0)
                return;
        }
    }

    /**
     * Reads until encountering a character that is not a whitespace according to the
     * <a href="https://spec.json5.org/#white-space">JSON5 Specification</a>
     *
     * @return a non-whitespace character, or {@code 0} if the end of the stream has been reached
     */
    public char nextClean() {
        while (true) {
            if (!more())
                throw syntaxError("Unexpected end of data");

            char n = next();

            if (n == '/') {
                char p = peek();

                if (p == '*') {
                    next();
                    nextMultiLineComment();
                } else if (p == '/') {
                    next();
                    nextSingleLineComment();
                } else return n;
            } else if (!isWhitespace(n))
                return n;
        }
    }

    private String nextCleanTo(String delimiters) {
        StringBuilder result = new StringBuilder();

        while (true) {
            if (!more())
                throw syntaxError("Unexpected end of data");

            char n = nextClean();

            if (delimiters.indexOf(n) > -1 || isWhitespace(n)) {
                back();
                break;
            }

            result.append(n);
        }

        return result.toString();
    }

    private int dehex(char c) {
        if (c >= '0' && c <= '9')
            return c - '0';

        if (c >= 'a' && c <= 'f')
            return c - 'a' + 0xA;

        if (c >= 'A' && c <= 'F')
            return c - 'A' + 0xA;

        return -1;
    }

    private char unicodeEscape(boolean member, boolean part) {
        String where = member ? "key" : "string";

        String value = "";
        int codepoint = 0;

        for (int i = 0; i < 4; ++i) {
            char n = next();
            value += n;

            int hex = dehex(n);

            if (hex == -1)
                throw syntaxError("Illegal unicode escape sequence '\\u" + value + "' in " + where);

            codepoint |= hex << ((3 - i) << 2);
        }

        if (member && !isMemberNameChar((char) codepoint, part))
            throw syntaxError("Illegal unicode escape sequence '\\u" + value + "' in key");

        return (char) codepoint;
    }

    private void checkSurrogate(char hi, char lo) {
        if (options.isAllowInvalidSurrogates())
            return;

        if (!Character.isHighSurrogate(hi) || !Character.isLowSurrogate(lo))
            return;

        if (!Character.isSurrogatePair(hi, lo))
            throw syntaxError(String.format(
                    "Invalid surrogate pair: U+%04X and U+%04X",
                    hi, lo
            ));
    }

    // https://spec.json5.org/#prod-JSON5String
    private String nextString(char quote) {
        StringBuilder result = new StringBuilder();

        String value;
        int codepoint;

        char n = 0;
        char prev;

        while (true) {
            if (!more())
                throw syntaxError("Unexpected end of data");

            prev = n;
            n = next();

            if (n == quote)
                break;

            if (isLineTerminator(n) && n != 0x2028 && n != 0x2029)
                throw syntaxError("Unescaped line terminator in string");

            if (n == '\\') {
                n = next();

                if (isLineTerminator(n)) {
                    if (n == '\r' && peek() == '\n')
                        next();

                    // escaped line terminator/ line continuation
                    continue;
                } else switch (n) {
                    case '\'':
                    case '"':
                    case '\\':
                        result.append(n);
                        continue;
                    case 'b':
                        result.append('\b');
                        continue;
                    case 'f':
                        result.append('\f');
                        continue;
                    case 'n':
                        result.append('\n');
                        continue;
                    case 'r':
                        result.append('\r');
                        continue;
                    case 't':
                        result.append('\t');
                        continue;
                    case 'v': // Vertical Tab
                        result.append((char) 0x0B);
                        continue;

                    case '0': // NUL
                        char p = peek();

                        if (isDecimalDigit(p))
                            throw syntaxError("Illegal escape sequence '\\0" + p + "'");

                        result.append((char) 0);
                        continue;

                    case 'x': // Hex escape sequence
                        value = "";
                        codepoint = 0;

                        for (int i = 0; i < 2; ++i) {
                            n = next();
                            value += n;

                            int hex = dehex(n);

                            if (hex == -1)
                                throw syntaxError("Illegal hex escape sequence '\\x" + value + "' in string");

                            codepoint |= hex << ((1 - i) << 2);
                        }

                        n = (char) codepoint;
                        break;

                    case 'u': // Unicode escape sequence
                        n = unicodeEscape(false, false);
                        break;

                    default:
                        if (isDecimalDigit(n))
                            throw syntaxError("Illegal escape sequence '\\" + n + "'");

                        break;
                }
            }

            checkSurrogate(prev, n);

            result.append(n);
        }

        return result.toString();
    }

    private boolean isMemberNameChar(char n, boolean part) {
        if (n == '$' || n == '_' || n == 0x200C || n == 0x200D)
            return true;

        int type = Character.getType(n);

        switch (type) {
            case Character.UPPERCASE_LETTER:
            case Character.LOWERCASE_LETTER:
            case Character.TITLECASE_LETTER:
            case Character.MODIFIER_LETTER:
            case Character.OTHER_LETTER:
            case Character.LETTER_NUMBER:
                return true;

            case Character.NON_SPACING_MARK:
            case Character.COMBINING_SPACING_MARK:
            case Character.DECIMAL_DIGIT_NUMBER:
            case Character.CONNECTOR_PUNCTUATION:
                if (part)
                    return true;
                break;
        }

        return false;
    }

    /**
     * Reads a member name from the source according to the
     * <a href="https://spec.json5.org/#prod-JSON5MemberName">JSON5 Specification</a>
     *
     * @return an member name
     */
    public String nextMemberName() {
        StringBuilder result = new StringBuilder();

        char prev;
        char n = next();

        if (n == '"' || n == '\'')
            return nextString(n);

        back();
        n = 0;

        while (true) {
            if (!more())
                throw syntaxError("Unexpected end of data");

            boolean part = result.length() > 0;

            prev = n;
            n = next();

            if (n == '\\') { // unicode escape sequence
                n = next();

                if (n != 'u')
                    throw syntaxError("Illegal escape sequence '\\" + n + "' in key");

                n = unicodeEscape(true, part);
            } else if (!isMemberNameChar(n, part)) {
                back();
                break;
            }

            checkSurrogate(prev, n);

            result.append(n);
        }

        if (result.length() == 0)
            throw syntaxError("Empty key");

        return result.toString();
    }

    /**
     * Reads a value from the source according to the
     * <a href="https://spec.json5.org/#prod-JSON5Value">JSON5 Specification</a>
     *
     * @return an member name
     */
    public Json5Element nextValue() {
        char n = nextClean();

        switch (n) {
            case '"':
            case '\'':
                String string = nextString(n);
                return new Json5String(string);
            case '{':
                back();
                return Json5Parser.parseObject(this);
            case '[':
                back();
                return Json5Parser.parseArray(this);
        }

        back();

        String string = nextCleanTo(",]}");

        if (string.equals("null"))
            return Json5Null.INSTANCE;

        if (PATTERN_BOOLEAN.matcher(string).matches())
            return new Json5Boolean(string.equals("true"));

        if (PATTERN_NUMBER_INTEGER.matcher(string).matches()) {
            BigInteger bigint = new BigInteger(string);
            return new Json5Number(bigint);
        }

        if (PATTERN_NUMBER_FLOAT.matcher(string).matches())
            return new Json5Number(new BigDecimal(string));

        if (PATTERN_NUMBER_SPECIAL.matcher(string).matches()) {
            String special;

            int factor;
            double d = 0;

            switch (string.charAt(0)) { // +, -, or 0
                case '+':
                    special = string.substring(1); // +
                    factor = 1;
                    break;

                case '-':
                    special = string.substring(1); // -
                    factor = -1;
                    break;

                default:
                    special = string;
                    factor = 1;
                    break;
            }

            switch (special) {
                case "NaN":
                    d = Double.NaN;
                    break;
                case "Infinity":
                    d = Double.POSITIVE_INFINITY;
                    break;
            }

            return new Json5Number(factor * d);
        }

        if (PATTERN_NUMBER_HEX.matcher(string).matches()) {
            return new Json5Hexadecimal(string);
        }

        throw new Json5Exception("Illegal value '" + string + "'");
    }

    /**
     * Constructs a new JSONException with a detail message and a causing exception
     *
     * @param message the detail message
     * @param cause   the causing exception
     * @return a JSONException
     */
    protected Json5Exception syntaxError(String message, Throwable cause) {
        return new Json5Exception(message + this, cause);
    }

    /**
     * Constructs a new JSONException with a detail message
     *
     * @param message the detail message
     * @return a JSONException
     */
    protected Json5Exception syntaxError(String message) {
        return new Json5Exception(message + this);
    }

    @Override
    public String toString() {
        return " at index " + index + " [character " + character + " in line " + line + "]";
    }
}

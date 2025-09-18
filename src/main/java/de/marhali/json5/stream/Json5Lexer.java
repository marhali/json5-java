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

package de.marhali.json5.stream;

import de.marhali.json5.*;
import de.marhali.json5.config.DigitSeparatorStrategy;
import de.marhali.json5.config.Json5Options;
import de.marhali.json5.exception.Json5Exception;
import de.marhali.json5.internal.RadixNumber;

import java.io.BufferedReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * This is a lexer to convert the provided data into tokens according to the json5 specification.
 * The resulting tokens can then be used in an appropriate parser to construct
 * {@link Json5Object}'s and {@link Json5Array}'s.
 *
 * @author SyntaxError404
 * @author Marcel Haßlinger
 * @see <a href="https://spec.json5.org/">Json5 Standard</a>.
 */
public class Json5Lexer {

    private final Reader reader;
    protected final Json5Options options;

    /**
     * whether we're currently parsing the root object/array
     */
    protected boolean root;

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
     * Current comment. Can be <code>null</code>.
     */
    private StringBuilder comment;

    /**
     * Constructs a new lexer from a specific {@link Reader}.
     * <p><b>Note:</b> The reader must be closed after operation ({@link Reader#close()})!</p>
     * @param reader  a reader.
     * @param options the options for lexing.
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
        comment = null;
    }

    /**
     * Returns the last comment that was read and clears it.
     *
     * @return The captured comment content, or <code>null</code> if no comment was found.
     */
    public String consumeComment() {
        if (comment == null) {
            return null;
        }

        String result = comment.toString().strip();
        comment = null;
        return result;
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
        if (comment == null) {
            comment = new StringBuilder();
        } else if (comment.length() != 0) {
            comment.append('\n');
        }

        while (true) {
            char n = next();

            if (n == 0) {
                throw syntaxError("Unterminated multi-line comment");
            }

            if (n == '*' && peek() == '/') {
                next();
                return;
            }

            comment.append(n);
        }
    }

    private void nextSingleLineComment() {
        if (comment == null) {
            comment = new StringBuilder();
        } else if (comment.length() != 0) {
            comment.append('\n');
        }

        while (true) {
            char n = next();

            if (isLineTerminator(n) || n == 0)
                return;

            comment.append(n);
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
                // TODO: check if empty stream, like index === -1?
                return 0;

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
            char n = nextClean();

            if (n == 0)
                return null;

            if (delimiters.indexOf(n) > -1 || isWhitespace(n)) {
                back();
                break;
            }

            result.append(n);
        }

        return result.toString();
    }

    private char[] unicodeEscape(boolean member, boolean part, boolean utf32) {
        if (utf32 && !options.isAllowLongUnicodeEscapes())
            throw syntaxError("Long unicode escape sequences are not allowed");

        String where = member ? "key" : "string";
        String escChar = utf32 ? "U" : "u";

        String value = "";
        int codepoint = 0;

        int numDigits = utf32 ? 8 : 4;

        for(int i = 0; i < numDigits; ++i) {
            char n = next();
            value += n;

            int hex = dehex(n);

            if (hex == -1)
                throw syntaxError("Illegal unicode escape sequence '\\" + escChar + value + "' in " + where);

            codepoint |= hex << ((numDigits - i - 1) << 2);
        }

        if (member && !isMemberNameChar((char) codepoint, part))
            throw syntaxError("Illegal unicode escape sequence '\\" + escChar + value + "' in key");

        return Character.toChars(codepoint);
    }

    private void checkSurrogate(char hi, char lo) {
        if (options.isAllowInvalidSurrogates())
            return;

        if ((Character.isHighSurrogate(hi) && !Character.isSurrogate(lo)) ||
            (!Character.isSurrogate(hi) && Character.isLowSurrogate(lo)))
            throw syntaxError(String.format(
                "Invalid surrogate pair: U+%04X and U+%04X",
                (int) hi, (int) lo
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
                throw syntaxError("Expected '" + quote + "' to close string, got EOF instead");

            prev = n;
            n = next();

            if (n == quote) {
                checkSurrogate(prev, (char) 0);
                break;
            }

            if (isLineTerminator(n) && n != 0x2028 && n != 0x2029)
                throw syntaxError("Unescaped line terminator in string");

            if (n == '\\') {
                n = next();

                if (isLineTerminator(n)) {
                    if (n == '\r' && peek() == '\n')
                        next();

                    // escaped line terminator/ line continuation
                    continue;
                }

                else switch(n) {
                    case 0:
                        throw syntaxError("Expected escape sequence in string, got EOF instead");

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

                            if (n == 0)
                                throw syntaxError("Expected hexadecimal digit for hexadecimal escape sequence in string, got EOF instead");

                            value += n;

                            int hex = dehex(n);

                            if (hex == -1)
                                throw syntaxError("Illegal hex escape sequence '\\x" + value + "' in string");

                            codepoint |= hex << ((1 - i) << 2);
                        }

                        n = (char) codepoint;
                        break;

                    case 'u': // Unicode escape sequence (16-bit)
                    case 'U': // Unicode escape sequence (32-bit)
                        char[] chars = unicodeEscape(false, false, n == 'U');

                        if (chars.length == 2) {
                            checkSurrogate(prev, chars[0]);
                            prev = chars[0];
                            n = chars[1];

                            result.append(prev);
                        }
                        else n = chars[0];

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

        if (n == 0)
            throw syntaxError("Expected key, got EOF instead");

        if (n == '"' || n == '\'')
            return nextString(n);

        back();
        n = 0;

        do {
            boolean part = result.length() > 0;

            prev = n;
            n = next();

            if (n == '\\') { // unicode escape sequence
                n = next();

                if (n == 0)
                    throw syntaxError("Expected escape sequence in key, got EOF instead");

                if (n != 'u' && n != 'U')
                    throw syntaxError("Illegal escape sequence '\\" + n + "' in key");

                char[] chars = unicodeEscape(true, part, n == 'U');

                if (chars.length == 2) {
                    checkSurrogate(prev, chars[0]);
                    prev = chars[0];
                    n = chars[1];

                    result.append(prev);
                }
                else n = chars[0];
            }
            else if (!isMemberNameChar(n, part)) {
                back();
                checkSurrogate(prev, (char) 0);
                break;
            }

            checkSurrogate(prev, n);

            result.append(n);
        } while(more());

        if (result.length() == 0)
            throw syntaxError("Expected key");

        return result.toString();
    }

    /**
     * Reads a value from the source according to the
     * <a href="https://spec.json5.org/#prod-JSON5Value">JSON5 Specification</a>
     *
     * @return a {@link Json5Element} value
     */
    public Json5Element nextValue() {
        char n = nextClean();
        boolean wasRoot = root;

        try {
            switch(n) {
                case '"':
                case '\'':
                    return Json5Primitive.fromString(nextString(n));
                case '{':
                    back();
                    root = false;
                    return Json5Parser.parseObject(this);
                case '[':
                    back();
                    root = false;
                    return Json5Parser.parseArray(this);
            }
        } finally {
            root = wasRoot;
        }

        back();

        String string = nextCleanTo(",]}");

        if (string == null)
            throw syntaxError("Expected value, got EOF instead");

        if (string.equals("null"))
            return Json5Primitive.fromNull();

        if (string.equals("true"))
            return Json5Primitive.fromBoolean(true);

        if (string.equals("false"))
            return Json5Primitive.fromBoolean(false);

        if (!string.isEmpty()) {
            char leading = string.charAt(0);
            String rest = string;

            double sign = 1;

            if(leading == '+') {
                rest = string.substring(1);
            }
            else if(leading == '-') {
                rest = string.substring(1);
                sign = -1;
            }

            if (rest.equals("Infinity")) {
                if (!options.isAllowInfinity())
                    throw syntaxError("Infinity is not allowed");

                return Json5Primitive.fromNumber(Math.copySign(Double.POSITIVE_INFINITY, sign));
            }

            if (rest.equals("NaN")) {
                if (!options.isAllowNaN())
                    throw syntaxError("NaN is not allowed");

                return Json5Primitive.fromNumber(Math.copySign(Double.NaN, sign));
            }

            if (!rest.isEmpty()) {
                leading = rest.charAt(0);

                if ((leading >= '0' && leading <= '9') || leading == '.') {
                    RadixNumber parsedNum = parseNumber(leading, rest);
                    Number num = parsedNum.getNumber();
                    int radix = parsedNum.getRadix();

                    if (sign < 0) {
                        if(num instanceof BigInteger)
                            return Json5Primitive.fromNumber(((BigInteger) num).negate(), radix);

                        if(num instanceof BigDecimal)
                            return Json5Primitive.fromNumber(((BigDecimal) num).negate(), radix);
                    }

                    return Json5Primitive.fromNumber(num, radix);
                }
            }
        }

        throw new Json5Exception("Illegal value '" + string + "'");
    }

    private RadixNumber parseNumber(char leading, String input) {
        BigInteger intValue = BigInteger.ZERO;

        int n = input.length();
        boolean floating = false;
        boolean hex = false;
        int off = 0;
        char c = 0;

        if (leading == '0') {
            if (n == 1)
                return new RadixNumber(intValue, 10);

            /************
             * PREFIXES *
             ************/
            switch(c = input.charAt(1)) {
                /**********
                 * BINARY *
                 **********/
                case 'b':
                case 'B':
                    if (!options.isAllowBinaryLiterals())
                        throw syntaxError("Binary literals are not allowed");

                    off = 2;

                    while (off < n) {
                        c = input.charAt(off++);

                        if (checkDigitSeparator(c)) {
                            if (off == 3 || off >= n || !isbin(input.charAt(off)))
                                throw syntaxError("Illegal position for digit separator");

                            continue;
                        }

                        if (!isbin(c))
                            throw syntaxError("Expected binary digit for literal");

                        intValue = intValue.shiftLeft(1);

                        if (c == '1')
                            intValue = intValue.setBit(0);
                    }

                    if (off == 2)
                        throw syntaxError("Expected binary digit after '0b'");

                    return new RadixNumber(intValue, 2);

                /*********
                 * OCTAL *
                 *********/
                case 'o':
                case 'O':
                    if (!options.isAllowOctalLiterals())
                        throw syntaxError("Octal literals are not allowed");

                    off = 2;

                    while (off < n) {
                        c = input.charAt(off++);

                        if (checkDigitSeparator(c)) {
                            if (off == 3 || off >= n || !isoct(input.charAt(off)))
                                throw syntaxError("Illegal position for digit separator");

                            continue;
                        }

                        if (!isoct(c))
                            throw syntaxError("Expected octal digit for literal");

                        intValue = intValue.shiftLeft(3);

                        if (c != '0')
                            intValue = intValue.or(BigInteger.valueOf(c - '0'));
                    }

                    if (off == 2)
                        throw syntaxError("Expected octal digit after '0o'");

                    return new RadixNumber(intValue, 8);


                /***************
                 * HEXADECIMAL *
                 ***************/
                case 'x':
                case 'X':
                    off = 2;
                    hex = true;

                    while (off < n) {
                        c = input.charAt(off++);

                        if (checkDigitSeparator(c)) {
                            if (off == 3 || off >= n || !ishex(input.charAt(off)))
                                throw syntaxError("Illegal position for digit separator");

                            continue;
                        }

                        if (c == '.' || c == 'p' || c == 'P') {
                            if (!options.isAllowHexFloatingLiterals())
                                throw syntaxError("Hexadecimal floating-point literals are not allowed");

                            floating = true;
                            break;
                        }

                        if  (!ishex(c))
                            throw syntaxError("Expected hexadecimal digit for literal");

                        intValue = intValue.shiftLeft(4);

                        if (c != '0')
                            intValue = intValue.or(BigInteger.valueOf(dehex(c)));
                    }

                    if (off == 2)
                        throw syntaxError("Expected hexadecimal digit after '0x'");

                    if (!floating)
                        return new RadixNumber(intValue, 16);

                    break;

                default:
                    break;
            };
        }

        StringBuilder num = new StringBuilder();

        if (!hex) {
            /***********
             * DECIMAL *
             ***********/
            while (off < n) {
                c = input.charAt(off++);

                if (checkDigitSeparator(c)) {
                    if (num.length() == 0 || off >= n || !isDecimalDigit(input.charAt(off)))
                        throw syntaxError("Illegal position for digit separator");

                    continue;
                }

                if (c == '.' || c == 'e' || c == 'E') {
                    floating = true;
                    break;
                }

                if (!isDecimalDigit(c))
                    throw syntaxError("Expected decimal digit for literal");

                num.append(c);
            }

            if (off >= n) {
                if (options.getDigitSeparatorStrategy() == DigitSeparatorStrategy.JAVA_STYLE)
                    input = input.replace("_", "");

                if (options.getDigitSeparatorStrategy() == DigitSeparatorStrategy.C_STYLE)
                    input = input.replace("'", "");

                return new RadixNumber(new BigInteger(input), 10);
            }
        }

        BigInteger fractionInt = BigInteger.ZERO;
        int numFracDigits = 0;

        if (c == '.') {
            /************
             * FRACTION *
             ************/
            if (!hex)
                num.append('.');

            while (off < n) {
                c = input.charAt(off++);

                if (checkDigitSeparator(c)) {
                    if (numFracDigits == 0 || off >= n)
                        throw syntaxError("Illegal position for digit separator");

                    c = input.charAt(off);

                    if ((!hex && !isDecimalDigit(c)) || (hex && !ishex(c)))
                        throw syntaxError("Illegal position for digit separator");

                    continue;
                }

                if (hex) {
                    if (c == 'p' || c == 'P')
                        break;

                    if (!ishex(c))
                        throw syntaxError("Expected hexadecimal digit for literal");

                    fractionInt = fractionInt.shiftLeft(4);

                    if (c != '0')
                        fractionInt = fractionInt.or(BigInteger.valueOf(dehex(c)));
                }
                else {
                    if (c == 'e' || c == 'E')
                        break;

                    if (!isDecimalDigit(c))
                        throw syntaxError("Expected decimal digit for literal");

                    num.append(c);
                }

                ++numFracDigits;
            }

            if (off >= n && !hex)
                return new RadixNumber(new BigDecimal(num.toString()), 10);
        }

        /************
         * EXPONENT *
         ************/
        if (hex && c != 'p' && c != 'P')
            throw syntaxError("Expected exponent for hexadecimal floating-point literal");

        if (!hex)
            num.append('e');

        int numExpDigits = 0;

        if (++off >= n)
            throw syntaxError("Expected digit sequence for exponent");

        c = input.charAt(off);

        if (c == '+' || c == '-') {
            num.append(c);
            ++off;
        }

        while (off < n) {
            c = input.charAt(off++);

            if (checkDigitSeparator(c)) {
                if( numExpDigits == 0 || off >= n || !isDecimalDigit(input.charAt(off)))
                    throw syntaxError("Illegal position for digit separator");

                continue;
            }

            if (!isDecimalDigit(c))
                throw syntaxError("Expected decimal digit for exponent");

            num.append(c);
            ++numExpDigits;
        }

        if (numExpDigits == 0)
            throw syntaxError("Expected digit sequence for exponent");

        if (!hex)
            return new RadixNumber(new BigDecimal(num.toString()), 10);

        /******************************
         * HEXADECIMAL FLOATING-POINT *
         ******************************/
        BigInteger exponent = new BigInteger(num.toString());
        BigDecimal value = new BigDecimal(intValue);

        BigDecimal two = BigDecimal.valueOf(2);
        BigDecimal frac = BigDecimal.valueOf(.5);

        for (int i = (4 * numFracDigits) - 1; i >= 0; --i) {
            if (fractionInt.testBit(i)) {
                value = value.add(frac);
            }

            frac = frac.divide(two);
        }

        BigDecimal scale;

        try {
            scale = new BigDecimal(BigInteger.TWO.pow(exponent.intValueExact()));
        }
        catch (Exception e) {
            throw syntaxError("Hexadecimal floating-point literal's exponent is too large");
        }

        return new RadixNumber(value.multiply(scale), 16);
    }

    private boolean checkDigitSeparator(char c) {
        if (c == '_') {
            if (options.getDigitSeparatorStrategy() != DigitSeparatorStrategy.JAVA_STYLE)
                throw syntaxError("Java-style digit separators are not allowed");

            return true;
        }

        if (c == '\'') {
            if (options.getDigitSeparatorStrategy() != DigitSeparatorStrategy.C_STYLE)
                throw syntaxError("C-style digit separators are not allowed");

            return true;
        }

        return false;
    }

    /**
     * Constructs a new {@link Json5Exception} with a detail message and a causing exception
     *
     * @param message the detail message
     * @param cause the causing exception
     * @return a {@link Json5Exception}
     */
    public Json5Exception syntaxError(String message, Throwable cause) {
        return new Json5Exception(message + this, cause);
    }

    /**
     * Constructs a new {@link Json5Exception} with a detail message
     *
     * @param message the detail message
     * @return a {@link Json5Exception}
     */
    public Json5Exception syntaxError(String message) {
        return new Json5Exception(message + this);
    }

    @Override
    public String toString() {
        return " at index " + index + " [character " + character + " in line " + line + "]";
    }

    private static int dehex(char c) {
        if(c >= '0' && c <= '9')
            return c - '0';

        if(c >= 'a' && c <= 'f')
            return c - 'a' + 0xA;

        if(c >= 'A' && c <= 'F')
            return c - 'A' + 0xA;

        return -1;
    }

    private static boolean isbin(char c) {
        return c == '0' || c == '1';
    }

    private static boolean isoct(char c) {
        return c >= '0' && c <= '7';
    }

    private static boolean ishex(char c) {
        return (c >= '0' && c <= '9')
            || (c >= 'a' && c <= 'f')
            || (c >= 'A' && c <= 'F');
    }

    /**
     * Converts a character into a string representation:
     *
     * <ul>
     * <li>if {@code c == 0}, {@code "EOF"} is returned</li>
     * <li>if {@code c} fulfills one of the following conditions, {@code "'x'"}
     *   is returned, where {@code x} is the value returned by {@link Character#toString(char)}:
     *   <ul>
     *   <li>if {@code c} is an extended ASCII character ({@code U+0001-U+00FF}),
     *       except {@link Character#isISOControl(char) control characters}</li>
     *   <li>if {@code c} is a {@link Character#isLetter(char) Unicode letter}</li>
     *   <li>if {@code c} is a {@link Character#isDigit(char) Unicode digit}</li>
     *   </ul>
     * </li>
     * <li>otherwise, {@code "U+XXXX"} is returned, where {@code XXXX} is the uppercase
     *     hexadecimal representation of {@code c}'s Unicode codepoint, padded with zeros
     *     ({@code 0}) to a length of 4 characters</li>
     * </ul>
     *
     * @param c the character
     * @return the string representation
     */
    protected static String charToString(char c) {
        if (c == 0)
            return "EOF";

        if ((c <= 0xFF && !Character.isISOControl(c)) || Character.isLetterOrDigit(c))
            return "'" + c + "'";

        return String.format("U+%04X", (int) c);
    }
}

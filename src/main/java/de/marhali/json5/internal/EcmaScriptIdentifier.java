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

/**
 * @author Marcel Haßlinger
 */
public class EcmaScriptIdentifier {
    // Zero Width Non-Joiner / Joiner
    private static final int ZWNJ = 0x200C;
    private static final int ZWJ  = 0x200D;

    private EcmaScriptIdentifier() {}

    /**
     * Checks whether the provided {@link String} is a valid ES5.1 IdentifierName.
     * @return true if valid identifier, otherwise false
     * @see <a href="https://262.ecma-international.org/5.1/#sec-7.6">https://262.ecma-international.org/5.1/#sec-7.6</a>
     */
    public static boolean isValid(String raw) {
        if (raw == null || raw.isEmpty()) return false;

        // Transform \\uXXXX-Escapes into real codepoints (ES5.1 allows escape in IdentifierName
        String unescaped = decodeEs5UnicodeEscapes(raw);
        if (unescaped == null || unescaped.isEmpty()) return false;

        int i = 0;
        int cp = unescaped.codePointAt(i);
        if (!isIdentifierStartES5(cp)) return false;
        i += Character.charCount(cp);

        while (i < unescaped.length()) {
            cp = unescaped.codePointAt(i);
            if (!isIdentifierPartES5(cp)) return false;
            i += Character.charCount(cp);
        }
        return true;
    }

    private static boolean isIdentifierStartES5(int cp) {
        // '$' and '_' explicit
        if (cp == '$' || cp == '_') return true;

        int t = Character.getType(cp);
        // Unicode categories: Lu, Ll, Lt, Lm, Lo, Nl
        switch (t) {
            case Character.UPPERCASE_LETTER:      // Lu
            case Character.LOWERCASE_LETTER:      // Ll
            case Character.TITLECASE_LETTER:      // Lt
            case Character.MODIFIER_LETTER:       // Lm
            case Character.OTHER_LETTER:          // Lo
            case Character.LETTER_NUMBER:         // Nl
                return true;
            default:
                return false;
        }
    }

    private static boolean isIdentifierPartES5(int cp) {
        if (isIdentifierStartES5(cp)) return true;
        if (cp == ZWNJ || cp == ZWJ) return true; // U+200C/U+200D are alowed

        int t = Character.getType(cp);
        // Additional categories: Mn, Mc, Nd, Pc
        switch (t) {
            case Character.NON_SPACING_MARK:        // Mn
            case Character.COMBINING_SPACING_MARK:  // Mc
            case Character.DECIMAL_DIGIT_NUMBER:    // Nd
            case Character.CONNECTOR_PUNCTUATION:   // Pc (e.g. underline, but already covered)
                return true;
            default:
                return false;
        }
    }

    /**
     * Decodes ES5-style Unicode-Escapes \\uXXXX inside a Identifier.
     * @return {@code null}, if an escape is syntactically invalid
     */
    private static String decodeEs5UnicodeEscapes(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length();) {
            char ch = s.charAt(i);
            if (ch == '\\') {
                if (i + 1 < s.length() && s.charAt(i + 1) == 'u') {
                    // Expect 4 hex chars (ES5.1; no \\u{...} syntax)
                    if (i + 6 > s.length()) return null;
                    String hex = s.substring(i + 2, i + 6);
                    int codeUnit = parse4Hex(hex);
                    if (codeUnit < 0) return null;
                    out.append((char) codeUnit);
                    i += 6;
                } else {
                    // Other backslashes are not allowed
                    return null;
                }
            } else {
                out.append(ch);
                i++;
            }
        }
        // Maybe Surrogates...
        return out.toString();
    }

    private static int parse4Hex(String hex4) {
        if (hex4.length() != 4) return -1;
        int val = 0;
        for (int i = 0; i < 4; i++) {
            char c = hex4.charAt(i);
            int d = Character.digit(c, 16);
            if (d < 0) return -1;
            val = (val << 4) | d;
        }
        return val;
    }
}

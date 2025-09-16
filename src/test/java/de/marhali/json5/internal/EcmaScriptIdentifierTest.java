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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
class EcmaScriptIdentifierTest {

    @Test
    @DisplayName("ASCII: valid identifiers")
    void asciiValid() {
        assertTrue(EcmaScriptIdentifier.isValid("foo"));
        assertTrue(EcmaScriptIdentifier.isValid("_bar"));
        assertTrue(EcmaScriptIdentifier.isValid("$baz"));
        assertTrue(EcmaScriptIdentifier.isValid("a1"));
        assertTrue(EcmaScriptIdentifier.isValid("A9_$_x"));
    }

    @Test
    @DisplayName("ASCII: invalid forms")
    void asciiInvalid() {
        assertFalse(EcmaScriptIdentifier.isValid(""));           // empty identifier
        assertFalse(EcmaScriptIdentifier.isValid("1abc"));       // starts with digit
        assertFalse(EcmaScriptIdentifier.isValid("with space")); // space
        assertFalse(EcmaScriptIdentifier.isValid("some-key"));   // hyphen
        assertFalse(EcmaScriptIdentifier.isValid("foo.bar"));    // dot
        assertFalse(EcmaScriptIdentifier.isValid("a,b"));        // comma
        assertFalse(EcmaScriptIdentifier.isValid("😀face"));     // symbol
    }

    @Test
    @DisplayName("Reserved words are allowed")
    void reservedWordsAllowed() {
        assertTrue(EcmaScriptIdentifier.isValid("class"));
        assertTrue(EcmaScriptIdentifier.isValid("default"));
        assertTrue(EcmaScriptIdentifier.isValid("function"));
        assertTrue(EcmaScriptIdentifier.isValid("if"));
        assertTrue(EcmaScriptIdentifier.isValid("true"));
        assertTrue(EcmaScriptIdentifier.isValid("false"));
        assertTrue(EcmaScriptIdentifier.isValid("null"));
        assertTrue(EcmaScriptIdentifier.isValid("NaN"));
        assertTrue(EcmaScriptIdentifier.isValid("Infinity"));
    }

    @Test
    @DisplayName("Unicode letters as start are allowed")
    void unicodeLettersStart() {
        assertTrue(EcmaScriptIdentifier.isValid("café"));         // Lo
        assertTrue(EcmaScriptIdentifier.isValid("naïve"));        // Lo + combining
        assertTrue(EcmaScriptIdentifier.isValid("äpfel"));        // Ll
        assertTrue(EcmaScriptIdentifier.isValid("Русский"));      // Cyrillic
        assertTrue(EcmaScriptIdentifier.isValid("你好"));          // CJK
        assertTrue(EcmaScriptIdentifier.isValid("ʰello"));        // Lm (U+02B0)
        assertTrue(EcmaScriptIdentifier.isValid("Ⅻwert"));        // Nl (U+216B)
    }

    @Test
    @DisplayName("Digit at start is invalid (even Unicode Nd)")
    void unicodeDigitStartInvalid() {
        assertFalse(EcmaScriptIdentifier.isValid("١abc")); // U+0661 ARABIC-INDIC DIGIT ONE (Nd) at start
    }

    @Test
    @DisplayName("IdentifierPart categories")
    void identifierPartCategories() {
        // digits (Nd) in part
        assertTrue(EcmaScriptIdentifier.isValid("foo1"));
        assertTrue(EcmaScriptIdentifier.isValid("a\u0661"));     // arabic-indic digit one in part

        // combining marks (Mn/Mc) in part
        assertTrue(EcmaScriptIdentifier.isValid("e\u0301"));     // 'e' + COMBINING ACUTE ACCENT (Mn)
        assertFalse(EcmaScriptIdentifier.isValid("\u0301e"));    // mark at start -> invalid

        // connector punctuation (Pc) in part
        assertTrue(EcmaScriptIdentifier.isValid("a_b"));         // U+005F LOW LINE
        assertTrue(EcmaScriptIdentifier.isValid("a\u203Fbc"));   // U+203F UNDERTIE (Pc)
        assertTrue(EcmaScriptIdentifier.isValid("a\u2054bc"));   // U+2054 INVERTED UNDERTIE (Pc)

        // Pc at start (not '_') is not allowed
        assertFalse(EcmaScriptIdentifier.isValid("\u203Fabc"));
    }

    @Test
    @DisplayName("ZWNJ/ZWJ allowed in part, not at start")
    void zwnjZwjRules() {
        assertTrue(EcmaScriptIdentifier.isValid("a\u200Cbc")); // ZWNJ
        assertTrue(EcmaScriptIdentifier.isValid("a\u200Dbc")); // ZWJ
        assertFalse(EcmaScriptIdentifier.isValid("\u200Cabc"));
        assertFalse(EcmaScriptIdentifier.isValid("\u200Dabc"));
    }

    @Test
    @DisplayName("Other Cf (format) not allowed")
    void otherCfNotAllowed() {
        assertFalse(EcmaScriptIdentifier.isValid("a\u00ADb")); // SOFT HYPHEN (Cf)
    }

    @Test
    @DisplayName("\\uXXXX escapes: valid")
    void unicodeEscapesValid() {
        assertTrue(EcmaScriptIdentifier.isValid("\\u0061bc"));       // 'a'bc
        assertTrue(EcmaScriptIdentifier.isValid("\\u00E4pfel"));     // 'ä'pfel
        assertTrue(EcmaScriptIdentifier.isValid("a\\u0301"));        // combining accent in part
        assertTrue(EcmaScriptIdentifier.isValid("a\\u200C"));        // ZWNJ in part
        assertTrue(EcmaScriptIdentifier.isValid("a\\u200D"));        // ZWJ in part
        assertTrue(EcmaScriptIdentifier.isValid("a\\u005Fb"));       // '_' in part
        assertTrue(EcmaScriptIdentifier.isValid("\\u0041\\u0030x")); // 'A''0'x
        // valid surrogate pair literal (astral letter) as UTF-16, not via \\u{...}
        assertTrue(EcmaScriptIdentifier.isValid("\uD801\uDC00abc")); // U+10400
    }

    @Test
    @DisplayName("\\uXXXX escapes: invalid or disallowed")
    void unicodeEscapesInvalid() {
        assertFalse(EcmaScriptIdentifier.isValid("\\u00G1"));     // not hex
        assertFalse(EcmaScriptIdentifier.isValid("\\u12"));       // too short
        assertFalse(EcmaScriptIdentifier.isValid("a\\x61"));      // \x not allowed
        assertFalse(EcmaScriptIdentifier.isValid("abc\\"));       // bare backslash
        assertFalse(EcmaScriptIdentifier.isValid("\\u200Cabc"));  // ZWNJ at start
        assertFalse(EcmaScriptIdentifier.isValid("\\uD800abc"));  // lone high surrogate
    }

    @Test
    @DisplayName("$ behaves like in JS")
    void dollarRules() {
        assertTrue(EcmaScriptIdentifier.isValid("$"));
        assertTrue(EcmaScriptIdentifier.isValid("$x"));
        assertTrue(EcmaScriptIdentifier.isValid("a$1"));
        assertTrue(EcmaScriptIdentifier.isValid("ä$"));
    }

    @Test
    @DisplayName("Null/empty")
    void nullAndEmpty() {
        assertFalse(EcmaScriptIdentifier.isValid(null));
        assertFalse(EcmaScriptIdentifier.isValid(""));
    }
}

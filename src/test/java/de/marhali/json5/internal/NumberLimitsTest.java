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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class NumberLimitsTest {
    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    @Test
    void parseBigInteger_allows_exactly_10000_chars() {
        String s = repeat('7', 10_000);
        BigInteger bi = assertDoesNotThrow(() -> NumberLimits.parseBigInteger(s));
        assertEquals(new BigInteger(s), bi);
    }

    @Test
    void parseBigInteger_rejects_length_over_10000_and_includes_prefix_in_message() {
        String s = repeat('9', 10_001);
        NumberFormatException ex =
            assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigInteger(s));
        String expectedPrefix = "Number string too large: " + s.substring(0, 30) + "...";
        assertTrue(ex.getMessage().startsWith(expectedPrefix),
            () -> "Msg mismatch.\nExpected prefix: " + expectedPrefix + "\nActual: " + ex.getMessage());
    }

    @Test
    void parseBigDecimal_allows_exactly_10000_chars() {
        String s = repeat('1', 10_000);
        BigDecimal bd = assertDoesNotThrow(() -> NumberLimits.parseBigDecimal(s));
        assertEquals(new BigDecimal(s), bd);
    }

    @Test
    void parseBigDecimal_rejects_length_over_10000_and_includes_prefix_in_message() {
        String s = repeat('3', 10_001);
        NumberFormatException ex =
            assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal(s));
        String expectedPrefix = "Number string too large: " + s.substring(0, 30) + "...";
        assertTrue(ex.getMessage().startsWith(expectedPrefix));
    }

    @Test
    void parseBigDecimal_allows_scale_abs_9999_both_signs() {
        BigDecimal bdNegExp = assertDoesNotThrow(() -> NumberLimits.parseBigDecimal("1E-9999"));
        assertEquals(9_999, bdNegExp.scale());

        BigDecimal bdPosExp = assertDoesNotThrow(() -> NumberLimits.parseBigDecimal("1E+9999"));
        assertEquals(-9_999, bdPosExp.scale());
    }

    @Test
    void parseBigDecimal_rejects_scale_10000_or_more_negative() {
        NumberFormatException ex =
            assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal("1E-10000"));
        assertTrue(ex.getMessage().contains("unsupported scale"));
    }

    @Test
    void parseBigDecimal_rejects_scale_10000_or_more_positive() {
        NumberFormatException ex =
            assertThrows(NumberFormatException.class, () -> NumberLimits.parseBigDecimal("1E+10000"));
        assertTrue(ex.getMessage().contains("unsupported scale"));
    }

    @Test
    void parseBigDecimal_parses_normal_numbers() {
        BigDecimal bd = assertDoesNotThrow(() -> NumberLimits.parseBigDecimal("12345.6789"));
        assertEquals(new BigDecimal("12345.6789"), bd);
    }

    @Test
    void parseBigInteger_parses_normal_numbers() {
        BigInteger bi = assertDoesNotThrow(() -> NumberLimits.parseBigInteger("-9007199254740993"));
        assertEquals(new BigInteger("-9007199254740993"), bi);
    }
}

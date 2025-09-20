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

package de.marhali.json5;

import de.marhali.json5.fixtures.ToStringFixtures;
import de.marhali.json5.internal.RadixNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class Json5PrimitiveTest {
    @Test
    @DisplayName("deepCopy(): it should copy primitive value and comment")
    void test_deepCopy() {
        var source = Json5Primitive.fromBoolean(true);
        String sourceComment = "my comment";
        source.setComment(sourceComment);

        Json5Element copy = source.deepCopy();
        String newComment = "new comment";
        source.setComment(newComment);

        assertTrue(copy.isJson5Primitive());
        assertTrue(copy.getAsJson5Primitive().isBoolean());
        assertTrue(copy.getAsJson5Primitive().getAsBoolean());
        assertEquals(sourceComment, copy.getComment());
        assertEquals(newComment, source.getComment());
    }

    @Test
    void test_equals() {
        var source = Json5Primitive.fromBoolean(true);
        source.setComment("my comment");

        var other = Json5Primitive.fromBoolean(true);
        other.setComment("my comment");

        assertEquals(source, other);
        assertNotEquals(source, Json5Primitive.fromBoolean(true));
    }

    @Nested
    class NullPrimitive {
        @Test
        @DisplayName("fromNull(): it should provide shorthand initializer to create Json5Null")
        void fromNull() {
            var element = Json5Primitive.fromNull();
            assertInstanceOf(Json5Null.class, element);
            assertEquals("null", element.getAsString());
            assertEquals("null", element.toString(ToStringFixtures.OPTIONS));
        }
    }

    @Nested
    class InstantPrimitive {
        @Test
        @DisplayName("fromInstant(): it should provide shorthand initializer to create primitive from Instant")
        void fromInstant() {
            var element = Json5Primitive.fromInstant(Instant.EPOCH);
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isInstant());
            assertEquals(Instant.EPOCH, element.getAsInstant());
            assertEquals("1970-01-01T00:00:00Z", element.getAsString());
            assertEquals("'1970-01-01T00:00:00Z'", element.toString(ToStringFixtures.OPTIONS));
        }
    }

    @Nested
    class BooleanPrimitive {
        @Test
        @DisplayName("fromBoolean(): it should provide shorthand initializer to create primitive from Boolean")
        void fromBoolean() {
            var element = Json5Primitive.fromBoolean(true);
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isBoolean());
            assertTrue(element.getAsBoolean());
            assertEquals("true", element.getAsString());
            assertEquals("true", element.toString(ToStringFixtures.OPTIONS));
        }
    }

    @Nested
    class StringPrimitive {
        @Test
        @DisplayName("fromCharacter(): it should provide shorthand initializer to create primitive from Character")
        void fromCharacter() {
            var element = Json5Primitive.fromCharacter('a');
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isString());
            assertEquals("a", element.getAsString());
            assertEquals("'a'", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        @DisplayName("fromString(): it should provide shorthand initializer to create primitive from String")
        void fromString() {
            var element = Json5Primitive.fromString("myString");
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isString());
            assertEquals("myString", element.getAsString());
            assertEquals("'myString'", element.toString(ToStringFixtures.OPTIONS));
        }
    }

    @Nested
    class NumberPrimitive {
        @Test
        void getAsNumberThrows() {
            assertThrows(UnsupportedOperationException.class, () -> Json5Primitive.fromBoolean(true).getAsNumber(), "Primitive is not a number nor a string");
        }

        @Test
        void getAsRadixNumberThrows() {
            assertThrows(UnsupportedOperationException.class, () -> Json5Primitive.fromBoolean(true).getAsRadixNumber(), "Primitive is not a number");
        }

        @Test
        @DisplayName("fromNumber(): it should provide shorthand initializer to create primitive from Number")
        void fromNumber() {
            var element = Json5Primitive.fromNumber(187);
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isNumber());
            assertEquals(187, element.getAsNumber());
            assertEquals(10, element.getNumberRadix());
            assertEquals("187", element.getAsString());
            assertEquals("187", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        @DisplayName("fromNumber(): it should provide shorthand initializer to create primitive from Number and radix base")
        void fromNumberWithRadix() {
            var element = Json5Primitive.fromNumber(187, 10);
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isNumber());
            assertEquals(new RadixNumber(187, 10), element.getAsRadixNumber());
            assertEquals(187, element.getAsNumber());
            assertEquals(10, element.getNumberRadix());
            assertEquals("187", element.getAsString());
            assertEquals("187", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void getAsDouble() {
            var element = Json5Primitive.fromNumber(1.87);
            assertEquals(1.87, element.getAsDouble());
        }

        @Test
        void getAsDoubleFromString() {
            var element = Json5Primitive.fromString("1.87");
            assertEquals(1.87, element.getAsDouble());
        }

        @Test
        void getAsBigDecimal() {
            var element = Json5Primitive.fromNumber(1.87);
            assertEquals(new BigDecimal("1.87"), element.getAsBigDecimal());
        }

        @Test
        void getAsBigDecimalFromString() {
            var element = Json5Primitive.fromString("1.87");
            assertEquals(new BigDecimal("1.87"), element.getAsBigDecimal());
        }

        @Test
        void getAsBigInteger() {
            var element = Json5Primitive.fromNumber(187);
            assertEquals(new BigInteger("187"), element.getAsBigInteger());
        }

        @Test
        void getAsBigIntegerFromString() {
            var element = Json5Primitive.fromString("187");
            assertEquals(new BigInteger("187"), element.getAsBigInteger());
        }

        @Test
        void getAsFloat() {
            var element = Json5Primitive.fromNumber(1.87);
            assertEquals(1.87f, element.getAsFloat());
        }

        @Test
        void getAsFloatFromString() {
            var element = Json5Primitive.fromString("1.87");
            assertEquals(1.87f, element.getAsFloat());
        }

        @Test
        void getAsLong() {
            var element = Json5Primitive.fromNumber(187L);
            assertEquals(187L, element.getAsLong());
        }

        @Test
        void getAsLongFromString() {
            var element = Json5Primitive.fromString("187");
            assertEquals(187L, element.getAsLong());
        }

        @Test
        void getAsShort() {
            var element = Json5Primitive.fromNumber((short) 187);
            assertEquals((short) 187, element.getAsShort());
        }

        @Test
        void getAsShortFromString() {
            var element = Json5Primitive.fromString("187");
            assertEquals((short) 187, element.getAsShort());
        }

        @Test
        void getAsInt() {
            var element = Json5Primitive.fromNumber(187);
            assertEquals((int) 187, element.getAsInt());
        }

        @Test
        void getAsIntFromString() {
            var element = Json5Primitive.fromString("187");
            assertEquals((int) 187, element.getAsInt());
        }

        @Test
        void getAsByte() {
            var element = Json5Primitive.fromNumber((byte) 187);
            assertEquals((byte) 187, element.getAsByte());
        }

        @Test
        void getAsByteFromString() {
            // 187 does not work, because it is a signed byte (+ / -)
            var element = Json5Primitive.fromString("87");
            assertEquals((byte) 87, element.getAsByte());
        }
    }

    @Nested
    class BinaryPrimitive {
        @Test
        @DisplayName("fromBinaryString(): it should provide shorthand initializer to create primitive from binary number string")
        void fromBinaryString() {
            var element = Json5Primitive.fromBinaryString("0b1010");
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isNumber());
            assertEquals(BigInteger.valueOf(10), element.getAsNumber());
            assertEquals(2, element.getNumberRadix());
            assertEquals("0b1010", element.getAsString());
            assertEquals("0b1010", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void fromPositiveBinaryString() {
            var element = Json5Primitive.fromBinaryString("+0b1010");
            assertInstanceOf(Json5Primitive.class, element);
            assertEquals(BigInteger.valueOf(10), element.getAsNumber());
            assertEquals(2, element.getNumberRadix());
            assertEquals("0b1010", element.getAsString());
            assertEquals("0b1010", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void fromNegateBinaryString() {
            var element = Json5Primitive.fromBinaryString("-0b1010");
            assertInstanceOf(Json5Primitive.class, element);
            assertEquals(BigInteger.valueOf(-10), element.getAsNumber());
            assertEquals(2, element.getNumberRadix());
            assertEquals("-0b1010", element.getAsString());
            assertEquals("-0b1010", element.toString(ToStringFixtures.OPTIONS));
        }
    }

    @Nested
    class OctalPrimitive {
        @Test
        @DisplayName("fromOctalString(): it should provide shorthand initializer to create primitive from octal number string")
        void fromOctalString() {
            var element = Json5Primitive.fromOctalString("0o273");
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isNumber());
            assertEquals(BigInteger.valueOf(187), element.getAsNumber());
            assertEquals(8, element.getNumberRadix());
            assertEquals("0o273", element.getAsString());
            assertEquals("0o273", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void fromPositiveOctalBinaryString() {
            var element = Json5Primitive.fromOctalString("+0o273");
            assertInstanceOf(Json5Primitive.class, element);
            assertEquals(BigInteger.valueOf(187), element.getAsNumber());
            assertEquals(8, element.getNumberRadix());
            assertEquals("0o273", element.getAsString());
            assertEquals("0o273", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void fromNegateOctalString() {
            var element = Json5Primitive.fromOctalString("-0o273");
            assertInstanceOf(Json5Primitive.class, element);
            assertEquals(BigInteger.valueOf(-187), element.getAsNumber());
            assertEquals(8, element.getNumberRadix());
            assertEquals("-0o273", element.getAsString());
            assertEquals("-0o273", element.toString(ToStringFixtures.OPTIONS));
        }
    }

    @Nested
    class HexPrimitive {
        @Test
        @DisplayName("fromHexString(): it should provide shorthand initializer to create primitive from hex number string")
        void fromHexString() {
            var element = Json5Primitive.fromHexString("0xBB");
            assertInstanceOf(Json5Primitive.class, element);
            assertTrue(element.isNumber());
            assertEquals(BigInteger.valueOf(187), element.getAsNumber());
            assertEquals(16, element.getNumberRadix());
            assertEquals("0xbb", element.getAsString());
            assertEquals("0xbb", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void fromPositiveHexBinaryString() {
            var element = Json5Primitive.fromHexString("+0xBB");
            assertInstanceOf(Json5Primitive.class, element);
            assertEquals(BigInteger.valueOf(187), element.getAsNumber());
            assertEquals(16, element.getNumberRadix());
            assertEquals("0xbb", element.getAsString());
            assertEquals("0xbb", element.toString(ToStringFixtures.OPTIONS));
        }

        @Test
        void fromNegateHexString() {
            var element = Json5Primitive.fromHexString("-0xBB");
            assertInstanceOf(Json5Primitive.class, element);
            assertEquals(BigInteger.valueOf(-187), element.getAsNumber());
            assertEquals(16, element.getNumberRadix());
            assertEquals("-0xbb", element.getAsString());
            assertEquals("-0xbb", element.toString(ToStringFixtures.OPTIONS));
        }
    }
}

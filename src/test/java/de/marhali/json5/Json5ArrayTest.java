/*
 * Copyright (C) 2022 - 2025 Marcel Haßlinger
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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class Json5ArrayTest {
    @Test
    void constructors_and_capacity_validation() {
        assertDoesNotThrow(() -> new Json5Array());
        assertDoesNotThrow(() -> new Json5Array(4));
        assertThrows(IllegalArgumentException.class, () -> new Json5Array(-1));
    }

    @Nested
    class AddOverloadsAndNullConversions {

        @Test
        void add_Instant_boolean_char_number_string_element_and_nulls() {
            Json5Array arr = new Json5Array();

            arr.add(Instant.EPOCH);
            arr.add(true);
            arr.add('X');
            arr.add(123);
            arr.add("hi");

            // nulls -> Json5Null
            arr.add((Instant) null);
            arr.add((Boolean) null);
            arr.add((Character) null);
            arr.add((Number) null);
            arr.add((String) null);
            arr.add((Json5Element) null); // generic add null

            assertEquals(11, arr.size());
            assertEquals(Json5Primitive.fromInstant(Instant.EPOCH), arr.get(0));
            assertEquals(Json5Primitive.fromBoolean(true), arr.get(1));
            assertEquals(Json5Primitive.fromCharacter('X'), arr.get(2));
            assertEquals(Json5Primitive.fromNumber(123), arr.get(3));
            assertEquals(Json5Primitive.fromString("hi"), arr.get(4));

            assertEquals(Json5Primitive.fromNull(), arr.get(5));
            assertEquals(Json5Primitive.fromNull(), arr.get(6));
            assertEquals(Json5Primitive.fromNull(), arr.get(7));
            assertEquals(Json5Primitive.fromNull(), arr.get(8));
            assertEquals(Json5Primitive.fromNull(), arr.get(9));
        }

        @Test
        void add_number_with_radix() {
            Json5Array arr = new Json5Array();
            arr.add(255, 16);
            assertEquals(Json5Primitive.fromNumber(255, 16), arr.get(0));
        }

        @Test
        void addAll_appends_in_order() {
            Json5Array a = new Json5Array();
            a.add(1);
            a.add(2);

            Json5Array b = new Json5Array();
            b.add(3);
            b.add(4);

            a.addAll(b);
            assertEquals(4, a.size());
            assertEquals(Json5Primitive.fromNumber(1), a.get(0));
            assertEquals(Json5Primitive.fromNumber(2), a.get(1));
            assertEquals(Json5Primitive.fromNumber(3), a.get(2));
            assertEquals(Json5Primitive.fromNumber(4), a.get(3));
        }
    }

    @Nested
    class SetRemoveContainsAndGet {

        @Test
        void set_replaces_and_returns_previous_converts_null() {
            Json5Array arr = new Json5Array();
            arr.add("a");
            arr.add("b");

            Json5Element previous = arr.set(1, Json5Primitive.fromNumber(7));
            assertEquals(Json5Primitive.fromString("b"), previous);
            assertEquals(Json5Primitive.fromNumber(7), arr.get(1));

            // set null -> Json5Null
            previous = arr.set(0, null);
            assertEquals(Json5Primitive.fromString("a"), previous);
            assertEquals(Json5Primitive.fromNull(), arr.get(0));
        }

        @Test
        void remove_by_element_and_index_and_contains() {
            Json5Array arr = new Json5Array();
            Json5Element one = Json5Primitive.fromNumber(1);
            arr.add(one);
            arr.add(one.deepCopy()); // same value, other instance
            arr.add("x");

            assertTrue(arr.contains(Json5Primitive.fromNumber(1)));
            assertEquals(3, arr.size());

            // remove(element) remove first occurrence
            boolean removed = arr.remove(Json5Primitive.fromNumber(1));
            assertTrue(removed);
            assertEquals(2, arr.size());
            assertEquals(Json5Primitive.fromNumber(1), arr.get(0)); // second element stays
            assertEquals(Json5Primitive.fromString("x"), arr.get(1));

            // remove(index)
            Json5Element rem = arr.remove(0);
            assertEquals(Json5Primitive.fromNumber(1), rem);
            assertEquals(1, arr.size());
            assertEquals(Json5Primitive.fromString("x"), arr.get(0));

            // remove not existing
            assertFalse(arr.remove(Json5Primitive.fromBoolean(true)));
        }

        @Test
        void get_and_bounds() {
            Json5Array arr = new Json5Array();
            arr.add("a");
            assertEquals(Json5Primitive.fromString("a"), arr.get(0));
            assertThrows(IndexOutOfBoundsException.class, () -> arr.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> arr.get(1));
            assertThrows(IndexOutOfBoundsException.class, () -> arr.remove(1));
            assertThrows(IndexOutOfBoundsException.class, () -> arr.set(1, Json5Primitive.fromNull()));
        }

        @Test
        void size_and_isEmpty_and_iterator_order() {
            Json5Array arr = new Json5Array();
            assertTrue(arr.isEmpty());
            assertEquals(0, arr.size());

            arr.add(10);
            arr.add(20);
            arr.add(30);

            assertFalse(arr.isEmpty());
            assertEquals(3, arr.size());

            Iterator<Json5Element> it = arr.iterator();
            assertTrue(it.hasNext());
            assertEquals(Json5Primitive.fromNumber(10), it.next());
            assertEquals(Json5Primitive.fromNumber(20), it.next());
            assertEquals(Json5Primitive.fromNumber(30), it.next());
            assertFalse(it.hasNext());
        }
    }

    @Nested
    class SingleElementGetters {

        @Test
        void getters_throw_if_not_singleton() {
            Json5Array empty = new Json5Array();
            Json5Array multi = new Json5Array();
            multi.add(1);
            multi.add(2);

            assertAll(
                () -> assertThrows(IllegalStateException.class, empty::getAsBoolean),
                () -> assertThrows(IllegalStateException.class, empty::getAsString),
                () -> assertThrows(IllegalStateException.class, multi::getAsNumber),
                () -> assertThrows(IllegalStateException.class, multi::getAsJson5Null)
            );
        }

        @Test
        void getters_delegate_when_singleton_primitive_number() {
            Json5Primitive prim = Json5Primitive.fromNumber(42);
            Json5Array arr = new Json5Array();
            arr.add(prim);

            assertEquals(42, arr.getAsInt());
            assertEquals(42L, arr.getAsLong());
            assertEquals(42.0, arr.getAsDouble());
            assertEquals((short) 42, arr.getAsShort());
            assertEquals((byte) 42, arr.getAsByte());
            assertEquals(42.0f, arr.getAsFloat());
            assertEquals(new BigInteger("42"), arr.getAsBigInteger());
            assertEquals(new BigDecimal("42"), arr.getAsBigDecimal());
            assertEquals("42", arr.getAsString());
            assertFalse(arr.getAsBoolean());
        }

        @Test
        void getters_delegate_radix_and_null_and_boolean() {
            Json5Array radix = new Json5Array();
            radix.add(255, 16);
            assertEquals(radix.get(0).getAsHexString(), radix.getAsHexString());
            assertEquals(radix.get(0).getAsOctalString(), radix.getAsOctalString());
            assertEquals(radix.get(0).getAsBinaryString(), radix.getAsBinaryString());
            assertEquals(radix.get(0).getAsRadixNumber().toString(),
                radix.getAsRadixNumber().toString());

            Json5Array nul = new Json5Array();
            nul.add((String) null);
            assertEquals(Json5Primitive.fromNull(), nul.getAsJson5Null());

            Json5Array bool = new Json5Array();
            bool.add(true);
            assertTrue(bool.getAsBoolean());
        }
    }

    @Nested
    class ListView {

        @Test
        void asList_is_mutable_and_bidirectional_and_disallows_nulls() {
            Json5Array arr = new Json5Array();
            arr.add("a");

            List<Json5Element> view = arr.asList();

            view.add(Json5Primitive.fromNumber(7));
            assertEquals(2, arr.size());
            assertEquals(Json5Primitive.fromNumber(7), arr.get(1));

            arr.add(false);
            assertEquals(Json5Primitive.fromBoolean(false), view.get(2));

            assertThrows(NullPointerException.class, () -> view.add(null));
        }
    }

    @Nested
    class DeepCopyEqualsHashCode {

        @Test
        void deepCopy_is_deep_and_copies_comment() {
            Json5Array original = new Json5Array();
            original.add(1);
            Json5Array inner = new Json5Array();
            inner.add("x");
            original.add(inner);
            original.setComment("note!");

            Json5Array copy = original.deepCopy();

            assertNotSame(original, copy);
            assertNotSame(original.get(1), copy.get(1)); // inner array deep-copied
            assertEquals(original, copy);
            assertEquals(original.hashCode(), copy.hashCode());
            assertEquals("note!", copy.getComment());

            // independent mutation
            ((Json5Array) copy.get(1)).set(0, Json5Primitive.fromString("changed"));
            assertNotEquals(original, copy);
        }

        @Test
        void equals_and_hashCode_contract() {
            Json5Array a = new Json5Array();
            a.add(1);
            a.add("x");

            Json5Array b = new Json5Array();
            b.add(1);
            b.add("x");

            Json5Array c = new Json5Array();
            c.add(1);
            c.add("x");

            assertEquals(a, a);           // reflexive
            assertEquals(a, b);           // symmetric
            assertEquals(b, a);
            assertEquals(b, c);           // transitive
            assertEquals(a, c);

            assertEquals(a.hashCode(), b.hashCode());
            assertEquals(a.hashCode(), c.hashCode());

            Json5Array d = new Json5Array();
            d.add(2);
            assertNotEquals(a, d);
        }
    }
}

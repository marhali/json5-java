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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class Json5ObjectTest {

    @Test
    void add_and_get_and_has_and_size() {
        Json5Object obj = new Json5Object();
        assertTrue(obj.isEmpty());
        assertEquals(0, obj.size());

        obj.add("a", Json5Primitive.fromString("x"));
        obj.add("b", Json5Primitive.fromNumber(42));

        assertFalse(obj.isEmpty());
        assertEquals(2, obj.size());

        assertTrue(obj.has("a"));
        assertTrue(obj.has("b"));
        assertFalse(obj.has("c"));

        assertEquals(Json5Primitive.fromString("x"), obj.get("a"));
        assertEquals(Json5Primitive.fromNumber(42), obj.get("b"));
        assertNull(obj.get("c"));
    }

    @Test
    void add_converts_null_to_json5null() {
        Json5Object obj = new Json5Object();
        obj.add("n", null);

        Json5Element got = obj.get("n");
        assertNotNull(got);
        assertEquals(Json5Primitive.fromNull(), got); // should point to Json5Null
    }

    @Test
    void remove_returns_removed_value_and_unsets() {
        Json5Object obj = new Json5Object();
        obj.add("k", Json5Primitive.fromBoolean(true));

        Json5Element removed = obj.remove("k");
        assertEquals(Json5Primitive.fromBoolean(true), removed);
        assertFalse(obj.has("k"));
        assertNull(obj.get("k"));

        assertNull(obj.remove("doesNotExist"));
    }

    @Test
    void entrySet_and_keySet_preserve_insertion_order() {
        Json5Object obj = new Json5Object();
        obj.add("first", Json5Primitive.fromNumber(1));
        obj.add("second", Json5Primitive.fromNumber(2));
        obj.add("third", Json5Primitive.fromNumber(3));

        // keySet order
        List<String> keys = new ArrayList<>(obj.keySet());
        assertEquals(List.of("first", "second", "third"), keys);

        // entrySet order
        Iterator<Map.Entry<String, Json5Element>> it = obj.entrySet().iterator();
        assertTrue(it.hasNext());
        assertEquals("first", it.next().getKey());
        assertEquals("second", it.next().getKey());
        assertEquals("third", it.next().getKey());
        assertFalse(it.hasNext());
    }

    @Nested
    class AddPropertyOverloads {

        @Test
        void addProperty_string() {
            Json5Object obj = new Json5Object();
            obj.addProperty("s", "hello");
            assertEquals(Json5Primitive.fromString("hello"), obj.get("s"));

            obj.addProperty("sn", (String) null);
            assertEquals(Json5Primitive.fromNull(), obj.get("sn"));
        }

        @Test
        void addProperty_number_default() {
            Json5Object obj = new Json5Object();
            obj.addProperty("n", 123);
            assertEquals(Json5Primitive.fromNumber(123), obj.get("n"));

            obj.addProperty("nn", (Number) null);
            assertEquals(Json5Primitive.fromNull(), obj.get("nn"));
        }

        @Test
        void addProperty_number_with_radix() {
            Json5Object obj = new Json5Object();
            obj.addProperty("r", 255, 16);
            assertEquals(Json5Primitive.fromNumber(255, 16), obj.get("r"));
        }

        @Test
        void addProperty_Instant() {
            Json5Object obj = new Json5Object();
            obj.addProperty("i", Instant.EPOCH);
            assertEquals(Json5Primitive.fromInstant(Instant.EPOCH), obj.get("i"));
        }

        @Test
        void addProperty_boolean() {
            Json5Object obj = new Json5Object();
            obj.addProperty("b1", true);
            obj.addProperty("b2", false);
            assertEquals(Json5Primitive.fromBoolean(true), obj.get("b1"));
            assertEquals(Json5Primitive.fromBoolean(false), obj.get("b2"));

            obj.addProperty("bn", (Boolean) null);
            assertEquals(Json5Primitive.fromNull(), obj.get("bn"));
        }

        @Test
        void addProperty_character() {
            Json5Object obj = new Json5Object();
            obj.addProperty("c", 'Z');
            assertEquals(Json5Primitive.fromCharacter('Z'), obj.get("c"));

            obj.addProperty("cn", (Character) null);
            assertEquals(Json5Primitive.fromNull(), obj.get("cn"));
        }
    }

    @Nested
    class TypedGetters {

        @Test
        void getAsJson5Primitive_and_type_casting() {
            Json5Object obj = new Json5Object();
            obj.addProperty("p", "str");
            assertEquals(Json5Primitive.fromString("str"), obj.getAsJson5Primitive("p"));

            // wrong type -> ClassCastException
            obj.add("arr", new Json5Array());
            assertThrows(ClassCastException.class, () -> obj.getAsJson5Primitive("arr"));
        }

        @Test
        void getAsJson5Array_and_getAsJson5Object() {
            Json5Object obj = new Json5Object();

            Json5Array array = new Json5Array();
            array.add(Json5Primitive.fromNumber(1));
            obj.add("a", array);

            Json5Object inner = new Json5Object();
            inner.addProperty("x", 7);
            obj.add("o", inner);

            assertSame(array, obj.getAsJson5Array("a"));
            assertSame(inner, obj.getAsJson5Object("o"));

            assertThrows(ClassCastException.class, () -> obj.getAsJson5Array("o"));
            assertThrows(ClassCastException.class, () -> obj.getAsJson5Object("a"));
        }
    }

    @Nested
    class MapView {

        @Test
        void asMap_is_mutable_and_bidirectional() {
            Json5Object obj = new Json5Object();
            obj.addProperty("a", 1);

            Map<String, Json5Element> view = obj.asMap();

            view.put("b", Json5Primitive.fromString("x"));
            assertTrue(obj.has("b"));
            assertEquals(Json5Primitive.fromString("x"), obj.get("b"));

            obj.addProperty("c", true);
            assertEquals(Json5Primitive.fromBoolean(true), view.get("c"));
        }

        @Test
        void asMap_disallows_null_keys_and_values() {
            Json5Object obj = new Json5Object();
            Map<String, Json5Element> view = obj.asMap();

            assertThrows(NullPointerException.class, () -> view.put(null, Json5Primitive.fromNull()));
            assertThrows(NullPointerException.class, () -> view.put("k", null));
        }
    }

    @Nested
    class DeepCopy_Equals_HashCode {

        @Test
        void deepCopy_is_deep_and_copies_comment() {
            Json5Object original = new Json5Object();
            original.addProperty("num", 1);
            Json5Object inner = new Json5Object();
            inner.addProperty("s", "t");
            original.add("inner", inner);
            original.setComment("note");

            Json5Object copy = original.deepCopy();

            // difference instances
            assertNotSame(original, copy);
            assertNotSame(original.getAsJson5Object("inner"), copy.getAsJson5Object("inner"));

            // same content
            assertEquals(original, copy);
            assertEquals(original.hashCode(), copy.hashCode());

            // same comment
            assertEquals("note", copy.getComment());

            // independent mutations
            copy.getAsJson5Object("inner").addProperty("s", "changed");
            assertNotEquals(original, copy);
            assertEquals("t",
                ((Json5Primitive) original.getAsJson5Object("inner").get("s")).getAsString());
        }

        @Test
        void equals_and_hashCode_contract() {
            Json5Object a = new Json5Object();
            a.addProperty("x", 1);
            a.addProperty("y", "z");

            Json5Object b = new Json5Object();
            b.addProperty("x", 1);
            b.addProperty("y", "z");

            Json5Object c = new Json5Object();
            c.addProperty("x", 1);
            c.addProperty("y", "z");

            // reflexive
            assertEquals(a, a);
            // symmetric
            assertEquals(a, b);
            assertEquals(b, a);
            // transitive
            assertEquals(b, c);
            assertEquals(a, c);

            // consistent to hashCode
            assertEquals(a.hashCode(), b.hashCode());
            assertEquals(a.hashCode(), c.hashCode());

            // not equals on not equal content
            Json5Object d = new Json5Object();
            d.addProperty("x", 2);
            assertNotEquals(a, d);
        }
    }

}

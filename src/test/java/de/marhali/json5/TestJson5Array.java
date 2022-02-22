/*
 * Copyright (C) 2022 Marcel Haßlinger
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

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Json5Array}
 *
 * @author Marcel Haßlinger
 */
public class TestJson5Array {

    @Test
    void deepCopy() {
        Json5Array array = new Json5Array();
        array.add(true);
        array.add(123);
        array.add(new Json5Hexadecimal("0x100"));
        array.add("Lorem ipsum");
        array.add(Json5Null.INSTANCE);
        array.add(new Json5Object());
        array.add(new Json5String("Lorem ipsum"));

        Json5Array target = array.deepCopy();

        assertEquals(new Json5Array(), new Json5Array());
        assertNotEquals(new Json5Array(), array);
        assertEquals(target, array);
        array.remove(1);
        assertNotEquals(target, array);
    }

    @Test
    void notAArray() {
        Json5Element element = new Json5Object();
        assertThrows(IllegalStateException.class, element::getAsJsonArray);
    }

    @Test
    void add() {
        Json5Array array = new Json5Array();
        array.add("Lorem ipsum");
        assertEquals(1, array.size());
        assertTrue(array.contains(Json5Primitive.of("Lorem ipsum")));
    }

    @Test
    void addAll() {
        Json5Array array = new Json5Array();
        array.add("Lorem ipsum");

        Json5Array target = new Json5Array();
        target.add("Lorem ipsum");
        target.add("Lorem ipsum");

        array.addAll(target);
        assertEquals(3, array.size());
    }

    @Test
    void getAsThrows() {
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsNumber()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsBigInteger()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsBigDecimal()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsDouble()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsFloat()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsLong()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsInt()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsShort()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsByte()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsBoolean()));
        assertThrows(IllegalStateException.class, (() -> new Json5Array().getAsString()));
    }

    @Test
    void getAsNumber() {
        Json5Array array = new Json5Array();
        array.add(123);
        assertEquals(123, array.getAsNumber());
    }

    @Test
    void getAsBigInt() {
        Json5Array array = new Json5Array();
        array.add(new BigInteger("123"));
        assertEquals(new BigInteger("123"), array.getAsBigInteger());
    }

    @Test
    void getAsBigDecimal() {
        Json5Array array = new Json5Array();
        array.add(new BigDecimal("123"));
        assertEquals(new BigDecimal("123"), array.getAsBigDecimal());
    }

    @Test
    void getAsDouble() {
        Json5Array array = new Json5Array();
        array.add(1.23d);
        assertEquals(1.23d, array.getAsDouble());
    }

    @Test
    void getAsFloat() {
        Json5Array array = new Json5Array();
        array.add(1.23f);
        assertEquals(1.23f, array.getAsFloat());
    }

    @Test
    void getAsLong() {
        Json5Array array = new Json5Array();
        array.add(123L);
        assertEquals(123L, array.getAsLong());
    }

    @Test
    void getAsInt() {
        Json5Array array = new Json5Array();
        array.add(123);
        assertEquals(123, array.getAsInt());
    }

    @Test
    void getAsShort() {
        Json5Array array = new Json5Array();
        array.add(Short.parseShort("123"));
        assertEquals(Short.parseShort("123"), array.getAsShort());
    }

    @Test
    void getAsByte() {
        Json5Array array = new Json5Array();
        array.add((byte)0x100);
        assertEquals((byte)0x100, array.getAsByte());
    }

    @Test
    void getAsBoolean() {
        Json5Array array = new Json5Array();
        array.add(true);
        assertTrue(array.getAsBoolean());
    }

    @Test
    void getAsString() {
        Json5Array array = new Json5Array();
        array.add("Lorem ipsum");
        assertEquals("Lorem ipsum", array.getAsString());
    }
}

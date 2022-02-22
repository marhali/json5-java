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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Json5} core class.
 *
 * @author Marcel Haßlinger
 */
public class TestJson5 {

    private Json5 json5;

    private InputStream getTestResource(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    private String getTestResourceContent(String fileName) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(getTestResource(fileName))) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }

            return buf.toString(StandardCharsets.UTF_8);
        }
    }

    @BeforeEach
    void setup() {
        json5 = Json5.builder(builder ->
                builder.allowInvalidSurrogate().quoteSingle().indentFactor(2).build());
    }

    @Test
    void parseString() {
        String jsonString = "{\n  'key': 'value',\n  'bool': true,\n  'hex': 0x100\n}";
        Json5Element element = json5.parse(jsonString);

        assertTrue(element.isJson5Object());
        assertEquals("value", element.getAsJson5Object().get("key").getAsString());
        assertTrue(element.getAsJson5Object().get("bool").getAsBoolean());
        assertInstanceOf(Json5Hexadecimal.class, element.getAsJson5Object().get("hex").getAsJson5Primitive());
    }

    @Test
    void serializeString() throws IOException {
        Json5Object element = new Json5Object();
        element.addProperty("key", "value");
        element.addProperty("bool", true);
        element.add("hex",  new Json5Hexadecimal("0x100"));

        String jsonString = json5.serialize(element);
        String expect = "{\n  'key': 'value',\n  'bool': true,\n  'hex': 0x100\n}";

        assertEquals(expect, jsonString);
    }

    @Test
    void ioArrayFile() throws IOException {
        try(InputStream stream = getTestResource("test.array.json5")) {
            Json5Element element = json5.parse(stream);
            assertTrue(element.isJson5Array());
            assertEquals(getTestResourceContent("expect.array.json5"), json5.serialize(element));
        }
    }

    @Test
    void ioObjectFile() throws IOException {
        try(InputStream stream = getTestResource("test.object.json5")) {
            Json5Element element = json5.parse(stream);
            assertTrue(element.isJson5Object());
            assertEquals(getTestResourceContent("expect.object.json5"), json5.serialize(element));
        }
    }
}

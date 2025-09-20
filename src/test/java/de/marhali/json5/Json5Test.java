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

import de.marhali.json5.config.Json5Options;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class Json5Test {

    // --- helpers ---
    private static Json5Object sampleObject() {
        Json5Object o = new Json5Object();
        o.addProperty("n", 42);
        o.addProperty("s", "hi");
        o.addProperty("b", true);
        Json5Array a = new Json5Array();
        a.add(1);
        a.add("x");
        o.add("arr", a);
        return o;
    }

    @Test
    void builder_applies_function_and_returns_instance() {
        Function<Json5Options.Builder, Json5Options> fn = b -> Json5Options.DEFAULT;
        Json5 j = Json5.builder(fn);
        assertNotNull(j);
        assertInstanceOf(Json5Object.class, j.parse("{}"));
    }

    @Test
    void constructor_with_options_and_default_and_null_checks() {
        assertDoesNotThrow(() -> new Json5(Json5Options.DEFAULT));
        assertDoesNotThrow(() -> new Json5());

        assertThrows(NullPointerException.class, () -> new Json5(null));
    }

    @Test
    void parse_from_string_object_array_and_empty() {
        Json5 json5 = new Json5();

        assertInstanceOf(Json5Object.class, json5.parse("{ }"));
        assertInstanceOf(Json5Array.class, json5.parse("[ ]"));

        assertNull(json5.parse(""));
    }

    @Test
    void parse_from_reader_and_stream() {
        Json5 json5 = new Json5();

        // Reader
        Reader r = new StringReader("{a:1}");
        Json5Element e1 = json5.parse(r);
        assertInstanceOf(Json5Object.class, e1);

        // InputStream
        InputStream in = new ByteArrayInputStream("[1,2]".getBytes());
        Json5Element e2 = json5.parse(in);
        assertInstanceOf(Json5Array.class, e2);
    }

    @Test
    void parse_null_arguments_throw_npe() {
        Json5 json5 = new Json5();
        assertThrows(NullPointerException.class, () -> json5.parse((String) null));
        assertThrows(NullPointerException.class, () -> json5.parse((Reader) null));
        assertThrows(NullPointerException.class, () -> json5.parse((InputStream) null));
    }

    @Test
    void serialize_null_arguments_throw_npe() {
        Json5 json5 = new Json5();
        Json5Element elem = sampleObject();

        assertThrows(NullPointerException.class, () -> json5.serialize(null));
        assertThrows(NullPointerException.class, () -> json5.serialize(null, new StringWriter()));
        assertThrows(NullPointerException.class, () -> json5.serialize(elem, (Writer) null));
        assertThrows(NullPointerException.class, () -> json5.serialize(null, new ByteArrayOutputStream()));
        assertThrows(NullPointerException.class, () -> json5.serialize(elem, (OutputStream) null));
    }
}

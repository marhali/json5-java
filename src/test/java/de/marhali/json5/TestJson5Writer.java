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

import de.marhali.json5.stream.Json5Writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Json5Writer}.
 *
 * @author Marcel Haßlinger
 */
public class TestJson5Writer {

    private final Json5Options options
            = new Json5Options(true, true, false, 0);

    private StringWriter stringWriter;
    private Json5Writer json5Writer;

    @BeforeEach
    void beforeEach() {
        stringWriter = new StringWriter();
        json5Writer = new Json5Writer(options, stringWriter);
    }

    @Test
    void quoteEmpty() {
        assertEquals("''", json5Writer.quote(null));
        assertEquals("''", json5Writer.quote(""));
    }

    @Test
    void quoteEscape() {
        assertEquals("'\\\\n'", json5Writer.quote("\\n"));
    }

    @Test
    void array() throws IOException {
        Json5Array array = new Json5Array();
        array.add(true);
        array.add(123);
        array.add(new Json5Hexadecimal("0x100"));
        array.add("Lorem ipsum");
        array.add(Json5Null.INSTANCE);
        array.add(new Json5Object());

        json5Writer.write(array);

        assertEquals("[true,123,0x100,'Lorem ipsum',null,{}]", stringWriter.toString());
    }

    @Test
    void object() throws IOException {
        Json5Object object = new Json5Object();
        object.add("bool", Json5Primitive.of(false));
        object.add("num", Json5Primitive.of(123));
        object.add("hex", new Json5Hexadecimal("0x100"));
        object.add("str", Json5Primitive.of("Lorem ipsum"));
        object.add("nulled", Json5Null.INSTANCE);
        object.add("array", new Json5Array());

        json5Writer.write(object);

        assertEquals("{'bool':false,'num':123,'hex':0x100,'str':'Lorem ipsum','nulled':null,'array':[]}",
                stringWriter.toString());
    }

    @Test
    void nullLiteral() throws IOException {
        json5Writer.write(Json5Null.INSTANCE);
        assertEquals("null", stringWriter.toString());
    }

    @Test
    void booleans() throws IOException {
        json5Writer.write(new Json5Boolean(false));
        assertEquals("false", stringWriter.toString());
    }

    @Test
    void largeNumber() throws IOException {
        json5Writer.write(new Json5Number(123e+45));
        assertEquals("1.23E47", stringWriter.toString());
    }

    @Test
    void largeNumberNegate() throws IOException {
        json5Writer.write(new Json5Number(-123e+45));
        assertEquals("-1.23E47", stringWriter.toString());
    }

    @Test
    void hexadecimal() throws IOException {
        json5Writer.write(new Json5Hexadecimal("0x100"));
        assertEquals("0x100", stringWriter.toString());
    }

    @Test
    void number() throws IOException {
        json5Writer.write(new Json5Number(123));
        assertEquals("123", stringWriter.toString());
    }

    @Test
    void string() throws IOException {
        json5Writer.write(new Json5String("Lorem ipsum"));
        assertEquals("'Lorem ipsum'", stringWriter.toString());
    }

    @Test
    void memberNames() throws IOException {
        Json5Object object = new Json5Object();
        object.addProperty("$LoremA_Ipsum123指事字", 0);
        json5Writer.write(object);
        assertEquals("{'$LoremA_Ipsum123指事字':0}", stringWriter.toString());
    }

    @Test
    void escapeChars() throws IOException {
        Json5Array array = new Json5Array();
        array.add("\\\n\r\f\b\t\u000B\u12fa\u000b");
        json5Writer.write(array);
        assertEquals("['\\\\\\n\\r\\f\\b\\t\\v\u12fa\\v']", stringWriter.toString());
        // Cannot test \x7F
    }
}

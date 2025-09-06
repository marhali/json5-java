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

import de.marhali.json5.exception.Json5Exception;
import de.marhali.json5.stream.Json5Lexer;
import de.marhali.json5.stream.Json5Parser;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Json5Parser}.
 *
 * @author Marcel Haßlinger
 */
public class TestJson5Parser {

    @Test
    void array() {
        String payload = "['hello',1,'two',{'key':'value'}]";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);
        assertEquals(payload, element.toString(options));
        assertTrue(element.get(0).getAsJson5Primitive() instanceof Json5String);
        assertTrue(element.get(1).getAsJson5Primitive() instanceof Json5Number);
        assertTrue(element.get(3).isJson5Object());
    }

    @Test
    void object() {
        String payload = "{'key':'value','array':['first','second'],'nested':{'key':'value'}}";

        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertEquals(payload, element.toString(options));
        assertEquals("value", element.get("key").getAsString());
        assertTrue(element.get("array").isJson5Array());
    }

    @Test
    void determineArrayType() {
        String payload = "['first','second']";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Element element = Json5Parser.parse(lexer);
        assertTrue(element.isJson5Array());
        assertInstanceOf(Json5Array.class, element);
    }

    @Test
    void determineObjectType() {
        String payload = "{'key':'value'}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Element element = Json5Parser.parse(lexer);
        assertTrue(element.isJson5Object());
        assertInstanceOf(Json5Object.class, element);
    }

    @Test
    void hexadecimal() {
        String payload = "{'key':0x100}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertEquals(payload, element.toString(options));
        assertInstanceOf(Json5Hexadecimal.class, element.getAsJson5Primitive("key"));
    }

    @Test
    void insideQuotes() {
        String payload = "[\"example\",'other']";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);
        assertEquals("['example','other']", element.toString(options));
    }

    @Test
    void mixedQuotes() {
        String payload = "{ a: \"Test \\' 123\" }";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertEquals("Test ' 123", element.get("a").getAsString());
    }

    @Test
    void escapeChars() {
        String payload = "{ a: \"\\n\\r\\f\\b\\t\\v\\0\u12fa\\x7F\" }";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertEquals("\n\r\f\b\t\u000B\0\u12fa\u007F", element.get("a").getAsString());
        assertInstanceOf(Json5String.class, element.get("a"));
    }

    @Test
    void specialNumbers() {
        String payload = "[+NaN,NaN,-NaN,+Infinity,Infinity,-Infinity]";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);
        assertEquals("[NaN,NaN,NaN,Infinity,Infinity,-Infinity]", element.toString(options));
        assertInstanceOf(Json5Number.class, element.get(0));
    }

    @Test
    void malformed() {
        String payload = "[10}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parse(lexer));
    }

    @Test
    void notAObject() {
        String payload = "[]";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseObject(lexer));
    }

    @Test
    void incompleteObject() {
        String payload = "{";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseObject(lexer));
    }

    @Test
    void notAArray() {
        String payload = "{}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseArray(lexer));
    }

    @Test
    void incompleteArray() {
        String payload = "[";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseArray(lexer));
    }

    @Test
    void duplicateObjectKeys() {
        String payload = "{'key':'value','key':'value'}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseObject(lexer));
    }

    @Test
    void noDivider() {
        String payload = "{'key''value'}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseObject(lexer));
    }

    @Test
    void noComma() {
        String payload = "{'key':'value''otherKey':'value'}";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parseObject(lexer));
    }

    @Test
    void unknownControlCharacter() {
        String payload = "|";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertThrows(Json5Exception.class, () -> Json5Parser.parse(lexer));
    }

    @Test
    void empty() {
        String payload = "";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        assertNull(Json5Parser.parse(lexer));
    }

    @Test
    void memberNames() {
        String payload = "{ $Lorem\\u0041_Ipsum123指事字: 0 }";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertTrue(element.has("$LoremA_Ipsum123指事字"));
    }

    @Test
    void multiComments() {
        String payload = "/**/{/**/a/**/:/**/'b'/**/}/**/";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertTrue(element.has("a"));
    }

    @Test
    void singleComments() {
        String payload = "// test\n{ // lorem ipsum\n a: 'b'\n// test\n}// test";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);
        assertTrue(element.has("a"));
    }

    @Test
    void booleans() {
        String payload = "[true,false]";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);
        assertEquals(payload, element.toString(options));
        assertInstanceOf(Json5Boolean.class, element.get(0));
    }

    @Test
    void numbers() {
        String payload = "[123e+45,-123e45,123]";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);
        assertEquals(123e+45, element.get(0).getAsNumber().doubleValue());
        assertEquals(-123e45, element.get(1).getAsNumber().doubleValue());
        assertEquals(123, element.get(2).getAsNumber().doubleValue());
        assertTrue(element.get(0).isJson5Primitive());
        assertTrue(element.get(0).getAsJson5Primitive().isNumber());
        assertInstanceOf(Json5Number.class, element.get(2));
    }

    @Test
    void nullLiteral() {
        String payload = "[null,{'key':null}]";
        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);
        assertEquals(payload, element.toString(options));
        assertTrue(element.get(0).isJson5Null());
        assertInstanceOf(Json5Null.class, element.get(0));
    }
}

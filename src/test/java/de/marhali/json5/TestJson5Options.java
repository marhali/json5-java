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

import de.marhali.json5.stream.Json5Lexer;
import de.marhali.json5.stream.Json5Parser;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to check if all {@link Json5Options} are applied correctly.
 *
 * @author Marcel Haßlinger
 */
public class TestJson5Options {

    @Test
    void singleQuoted() {
        String payload = "['hello',1,'two']";

        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);

        assertEquals("['hello',1,'two']", element.toString(options));
    }

    @Test
    void doubleQuoted() {
        String payload = "['hello',1,'two']";

        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, false, false, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);

        assertEquals("[\"hello\",1,\"two\"]", element.toString(options));
    }

    @Test
    void trailingComma() {
        String payload = "['hello',1,'two']";

        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, true, 0, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);

        assertEquals("['hello',1,'two',]", element.toString(options));
    }

    @Test
    void prettyPrinting() {
        String payload = "['hello',1,'two']";

        //<editor-fold desc="Modified by Ultreon (added support for quoteless)">
        Json5Options options = new Json5Options(true, true, true, 2, false);
        //</editor-fold>
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Array element = Json5Parser.parseArray(lexer);

        assertEquals("[\n  'hello',\n  1,\n  'two',\n]", element.toString(options));
    }

    //<editor-fold desc="Modified by Ultreon (added test for quoteless)">
    @Test
    void quoteless() {
        String payload = "{hello: 'world', key: 'value'}";

        Json5Options options = new Json5Options(true, true, true, 2, true);
        Json5Lexer lexer = new Json5Lexer(new StringReader(payload), options);
        Json5Object element = Json5Parser.parseObject(lexer);

        assertEquals("{\n  hello: 'world',\n  key: 'value',\n}", element.toString(options));
    }
    //</editor-fold>

    //<editor-fold desc="Modified by Ultreon (added test for quoteless and comments)">
    @Test
    void quotelessAndComment() {
        Json5Options options = new Json5Options(true, true, true, 2, true);
        Json5Object element = new Json5Object();

        element.add("hello", Json5Primitive.of("world"));
        element.add("key", Json5Primitive.of("value"));
        element.setComment("key", "This is a comment\nAnd another comment");

        assertEquals("{\n  hello: 'world',\n  // This is a comment\n  // And another comment\n  key: 'value',\n}", element.toString(options));
    }
    //</editor-fold>
}
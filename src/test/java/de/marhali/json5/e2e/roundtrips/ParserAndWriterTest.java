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

package de.marhali.json5.e2e.roundtrips;

import de.marhali.json5.Json5;
import de.marhali.json5.config.Json5Options;
import de.marhali.json5.e2e.TestResourceHelper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcel Haßlinger
 */
public class ParserAndWriterTest {
    @Test
    void objectRoundtrip() throws IOException {
        var json5 = new Json5();

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/object.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/object.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void objectRoundtripNoComment() throws IOException {
        var json5 = new Json5(Json5Options.builder()
            .allowNaN()
            .allowInfinity()
            .parseComments()
            .trailingComma()
            .indentFactor(2)
            .quoteless()
            .insertFinalNewline()
            .build()
        );

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/object.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/object-no-comment.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void objectRoundtripMinifyNoComment() throws IOException {
        var json5 = new Json5(Json5Options.builder()
            .allowNaN()
            .allowInfinity()
            .parseComments()
            .indentFactor(0)
            .quoteless()
            .insertFinalNewline()
            .build()
        );

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/object.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/object-minify-no-comment.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void objectRoundtripMinify() throws IOException {
        var json5 = new Json5(Json5Options.builder()
            .allowNaN()
            .allowInfinity()
            .parseComments()
            .writeComments()
            .indentFactor(0)
            .quoteless()
            .insertFinalNewline()
            .build()
        );

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/object.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/object-minify.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void objectRoundtripMinifyToPretty() throws IOException {
        var json5 = new Json5();

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/object-minify.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/object-minify-to-prettify.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void arrayRoundtrip() throws IOException {
        var json5 = new Json5();

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/array.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/array.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void arrayRoundtripNoComment() throws IOException {
        var json5 = new Json5(Json5Options.builder()
            .allowNaN()
            .allowInfinity()
            .parseComments()
            .trailingComma()
            .indentFactor(2)
            .quoteless()
            .insertFinalNewline()
            .build()
        );

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/array.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/array-no-comment.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void arrayRoundtripMinifyNoComment() throws IOException {
        var json5 = new Json5(Json5Options.builder()
            .allowNaN()
            .allowInfinity()
            .parseComments()
            .indentFactor(0)
            .quoteless()
            .insertFinalNewline()
            .build()
        );

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/array.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/array-minify-no-comment.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void arrayRoundtripMinify() throws IOException {
        var json5 = new Json5(Json5Options.builder()
            .allowNaN()
            .allowInfinity()
            .parseComments()
            .writeComments()
            .indentFactor(0)
            .quoteless()
            .insertFinalNewline()
            .build()
        );

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/array.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/array-minify.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }

    @Test
    void arrayRoundtripMinifyToPretty() throws IOException {
        var json5 = new Json5();

        var element = json5.parse(TestResourceHelper.getTestResource("e2e/roundtrips/array-minify.parser.json5"));
        var stringifiedElement = json5.serialize(element);

        var expectedStringifiedElement = TestResourceHelper.getTestResourceContent("e2e/roundtrips/array-minify-to-prettify.writer.json5");

        assertEquals(expectedStringifiedElement, stringifiedElement);
    }
}

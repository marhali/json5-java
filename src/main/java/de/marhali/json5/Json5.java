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
import de.marhali.json5.stream.Json5Writer;

import java.io.*;
import java.util.Objects;
import java.util.function.Function;

/**
 * This is the main class for using JSON5. This class provides methods to parse and
 * serialize Json5 data according to the specification and the configured {@link Json5Options options}.
 *
 * <p>
 *     You can create a Json5 instance by invoking {@link #Json5(Json5Options)}
 *     or by using {@link #builder(Function)}.
 *     </p>
 *
 * <p>
 *     This class contains several utility methods to parse and serialize json5 data by passing
 *     {@link Reader}, {@link Writer} or simple {@link String} instances.
 * </p>
 *
 * @author Marcel Haßlinger
 * @see <a href="https://spec.json5.org/">JSON5 Specification</a>
 * @see Json5Parser
 * @see Json5Writer
 */
public final class Json5 {

    /**
     * Constructs a new json5 instance by using the {@link Json5OptionsBuilder}.
     * @param builder Options builder
     * @return Provide built options by returning {@link Json5OptionsBuilder#build()} method
     */
    public static Json5 builder(Function<Json5OptionsBuilder, Json5Options> builder) {
        return new Json5(builder.apply(new Json5OptionsBuilder()));
    }

    private final Json5Options options;

    /**
     * Constructs a new json5 instance with custom configuration for parsing and serialization.
     * @param options Configuration options
     * @see #builder(Function)
     */
    public Json5(Json5Options options) {
        this.options = Objects.requireNonNull(options);
    }

    /**
     * Constructs a json5 instance by using {@link Json5Options#DEFAULT} as configuration.
     * @see #Json5(Json5Options)
     */
    public Json5() {
        this(Json5Options.DEFAULT);
    }

    /**
     * Parses the data from the {@link InputStream} into a tree of {@link Json5Element}'s. There must be
     * a root element based on a {@link Json5Object} or {@link Json5Array}.
     * <p><b>Note:</b> The stream must be closed after operation</p>
     * @param in Can be any applicable {@link InputStream}
     * @return Parsed json5 tree. Can be {@code null} if the provided stream does not contain any data
     * @see #parse(Reader)
     */
    public Json5Element parse(InputStream in) {
        Objects.requireNonNull(in);
        return parse(new InputStreamReader(in));
    }

    /**
     * Parses the provided read-stream into a tree of {@link Json5Element}'s. There must be
     * a root element based on a {@link Json5Object} or {@link Json5Array}.
     * <p><b>Note:</b> The reader must be closed after operation</p>
     * @param reader Can be any applicable {@link Reader}
     * @return Parsed json5 tree. Can be {@code null} if the provided stream does not contain any data
     * @see Json5Parser#parse(Json5Lexer) 
     */
    public Json5Element parse(Reader reader) {
        Objects.requireNonNull(reader);

        Json5Lexer lexer = new Json5Lexer(reader, this.options);
        return Json5Parser.parse(lexer);
    }

    /**
     * Parses the provided json5-encoded {@link String} into a parse tree of {@link Json5Element}'s.
     * There must be a root element based on a {@link Json5Object} or {@link Json5Array}.
     * @param jsonString Json5 encoded {@link String}
     * @return Parsed json5 tree. Can be {@code null} if the provided {@link String} is empty
     * @see #parse(Reader) 
     */
    public Json5Element parse(String jsonString) {
        Objects.requireNonNull(jsonString);

        StringReader reader = new StringReader(jsonString);
        Json5Element element = this.parse(reader);
        reader.close();
        return element;
    }

    /**
     * Encodes the provided element into its character literal representation by using an output-stream.
     * <p><b>Note:</b> The stream must be closed after operation ({@link OutputStream#close()})!</p>
     * @param element {@link Json5Element} to serialize
     * @param out Can be any applicable {@link OutputStream}
     * @throws IOException If an I/O error occurs
     * @see #serialize(Json5Element, Writer)
     */
    public void serialize(Json5Element element, OutputStream out) throws IOException {
        Objects.requireNonNull(element);
        Objects.requireNonNull(out);

        serialize(element, new OutputStreamWriter(out));
    }

    /**
     * Encodes the provided element into its character literal representation by using a write-stream.
     * <p><b>Note:</b> The writer must be closed after operation ({@link Writer#close()})!</p>
     * @param element {@link Json5Element} to serialize
     * @param writer Can be any applicable {@link Writer}
     * @throws IOException If an I/O error occurs
     * @see Json5Writer#write(Json5Element) 
     */
    public void serialize(Json5Element element, Writer writer) throws IOException {
        Objects.requireNonNull(element);
        Objects.requireNonNull(writer);

        Json5Writer json5Writer = new Json5Writer(this.options, writer);
        json5Writer.write(element);
    }

    /**
     * Encodes the provided element into its character literal representation.
     * @param element {@link Json5Element} to serialize
     * @return Json5 encoded {@link String}
     * @throws IOException If an I/O error occurs
     * @see #serialize(Json5Element, Writer) 
     */
    public String serialize(Json5Element element) throws IOException {
        Objects.requireNonNull(element);

        StringWriter writer = new StringWriter();
        serialize(element, writer);
        writer.close();
        return writer.toString();
    }
}
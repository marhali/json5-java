/*
 * MIT License
 *
 * Copyright (C) 2021 SyntaxError404
 * Copyright (C) 2022 Marcel Haßlinger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.marhali.json5.stream;

import de.marhali.json5.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

/**
 * Writes a tree of {@link Json5Element}'s into their
 * string literal representation by using a {@link Writer write} stream.
 *
 * @author Marcel Haßlinger
 * @author SyntaxError404
 */
public final class Json5Writer {

    private final Json5Options options;
    private final Writer writer;

    /**
     * Creates a new instance that writes a JSON5-encoded stream to {@code writer}.
     * @param options Parsing and serialization options
     * @param writer Output stream. For best performance, use a {@link java.io.BufferedWriter}
     * @apiNote The writer must be closed after operation ({@link Writer#close()})!
     */
    public Json5Writer(Json5Options options, Writer writer) {
        this.options = Objects.requireNonNull(options);
        this.writer = Objects.requireNonNull(writer);
    }

    /**
     * Encodes and writes the provided {@link Json5Element} into json5 according to the specification
     * and the configured options. The element can be any json5 element. All child trees will be included.
     * @param element Element to encode
     * @throws IOException If an I/O error occurs
     * @see #Json5Writer(Json5Options, Writer) Configuration options
     * @see #write(Json5Element, String)
     */
    public void write(Json5Element element) throws IOException {
        write(element, "");
    }

    /**
     * Encodes and writes the provided {@link Json5Element} into json5 according to the specification
     * and the configured options. The element can be any json5 element. All child trees will be included.
     * @param element Element to encode
     * @param indent Indent to apply (for nested elements)
     * @throws IOException If an I/O error occurs
     * @see #Json5Writer(Json5Options, Writer) Configuration options
     * @see #write(Json5Element) without indent
     */
    public void write(Json5Element element, String indent) throws IOException {
        Objects.requireNonNull(element);
        Objects.requireNonNull(indent);

        if(element.isJsonNull()) {
            writeNull();
        } else if(element.isJsonObject()) {
            writeObject(element.getAsJsonObject(), indent);
        } else if(element.isJsonArray()) {
            writeArray(element.getAsJsonArray(), indent);
        } else if(element.isJsonPrimitive()) {
            writePrimitive(element.getAsJsonPrimitive());
        } else {
            throw new UnsupportedOperationException("Unknown json element with type class "
                    + element.getClass().getName());
        }
    }

    /**
     * Writes the equivalent of a {@link de.marhali.json5.Json5Null}({@code null}) value.
     * @throws IOException If an I/O error occurs.
     */
    public void writeNull() throws IOException {
        writer.write("null");
    }

    /**
     * Writes the provided primitive to the stream and encodes it if necessary.
     * @param primitive Primitive value.
     * @throws IOException If an I/O error occurs.
     */
    public void writePrimitive(Json5Primitive primitive) throws IOException {
        Objects.requireNonNull(primitive);

        if(primitive instanceof Json5String) {
            writer.append(quote(primitive.getAsString()));
        } else {
            writer.append(primitive.getAsString());
        }
    }

    /**
     * Writes the provided {@link Json5Object} to the stream.
     * @param object Object to encode
     * @param indent Indent to apply (for nested elements)
     * @throws IOException If an I/O error occurs.
     * @see #write(Json5Element)
     */
    public void writeObject(Json5Object object, String indent) throws IOException {
        Objects.requireNonNull(object);
        Objects.requireNonNull(indent);

        String childIndent = indent + " ".repeat(options.getIndentFactor());

        writer.write("{");

        int index = 0;
        for(Map.Entry<String, Json5Element> entry : object.entrySet()) {
            if(options.getIndentFactor() > 0) {
                writer.append('\n').append(childIndent);
            }

            writer.append(quote(entry.getKey())).append(":");

            if(options.getIndentFactor() > 0) {
                writer.append(' ');
            }

            write(entry.getValue(), childIndent);

            if(options.isTrailingComma() || index < object.size() - 1) {
                writer.append(',');
            }

            index++;
        }

        if(options.getIndentFactor() > 0) {
            writer.append('\n').append(indent);
        }

        writer.append('}');
    }

    /**
     * Writes the provided {@link Json5Array} to the stream.
     * @param array Array to encode
     * @param indent Indent to apply (for nested elements)
     * @throws IOException If an I/O error occurs.
     * @see #write(Json5Element)
     */
    public void writeArray(Json5Array array, String indent) throws IOException {
        Objects.requireNonNull(array);
        Objects.requireNonNull(indent);

        String childIndent = indent + " ".repeat(options.getIndentFactor());

        writer.write('[');

        for(int i = 0; i < array.size(); i++) {
            Json5Element currentElement = array.get(i);

            if(options.getIndentFactor() > 0) {
                writer.append('\n').append(childIndent);
            }

            write(currentElement, childIndent);

            if(options.isTrailingComma() || i < array.size() - 1) {
                writer.append(',');
            }
        }

        if(options.getIndentFactor() > 0) {
            writer.append('\n').append(indent);
        }

        writer.write(']');
    }

    /**
     * Quotes the provided string according to the json5 <a href="https://spec.json5.org/#strings">specification</a>.
     * @param string String to quote
     * @return Quoted string
     */
    public String quote(String string) {
        final char qt = options.isQuoteSingle() ? '\'' : '"';

        if(string == null || string.isEmpty()) {
            return String.valueOf(qt).repeat(2);
        }

        StringBuilder quoted = new StringBuilder(string.length() + 2);
        quoted.append(qt);

        for(char c : string.toCharArray()) {
            if(c == qt) {
                quoted.append('\\');
                quoted.append(c);
                continue;
            }

            switch(c) {
                case '\\':
                    quoted.append("\\\\");
                    break;
                case '\b':
                    quoted.append("\\b");
                    break;
                case '\f':
                    quoted.append("\\f");
                    break;
                case '\n':
                    quoted.append("\\n");
                    break;
                case '\r':
                    quoted.append("\\r");
                    break;
                case '\t':
                    quoted.append("\\t");
                    break;
                case 0x0B: // Vertical Tab
                    quoted.append("\\v");
                    break;
                default:
                    // escape non-graphical characters (https://www.unicode.org/versions/Unicode13.0.0/ch02.pdf#G286941)
                    switch(Character.getType(c)) {
                        case Character.FORMAT:
                        case Character.LINE_SEPARATOR:
                        case Character.PARAGRAPH_SEPARATOR:
                        case Character.CONTROL:
                        case Character.PRIVATE_USE:
                        case Character.SURROGATE:
                        case Character.UNASSIGNED:
                            quoted.append("\\u");
                            quoted.append(String.format("%04X", c));
                            break;
                        default:
                            quoted.append(c);
                            break;
                    }
            }
        }

        quoted.append(qt);
        return quoted.toString();
    }
}
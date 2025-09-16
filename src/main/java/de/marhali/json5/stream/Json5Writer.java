/*
 * MIT License
 *
 * Copyright (C) 2021 SyntaxError404
 * Copyright (C) 2024 Ultreon Team
 * Copyright (C) 2022 - 2025 Marcel Haßlinger
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
import de.marhali.json5.config.Json5Options;
import de.marhali.json5.internal.EcmaScriptIdentifier;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;

/**
 * Writes a tree of {@link Json5Element}'s into their
 * string literal representation by using a {@link Writer write} stream.
 *
 * @author SyntaxError404
 * @author Ultreon Team
 * @author Marcel Haßlinger
 */
public final class Json5Writer {

    private final Json5Options options;
    private final Writer writer;

    /**
     * Creates a new instance that writes a JSON5-encoded stream to {@code writer}.
     * <p><b>Note:</b> The writer must be closed after operation ({@link Writer#close()})!</p>
     * @param options Parsing and serialization options
     * @param writer Output stream. For the best performance, use a {@link java.io.BufferedWriter}.
     */
    public Json5Writer(Json5Options options, Writer writer) {
        Objects.requireNonNull(options);
        Objects.requireNonNull(writer);

        this.options = options;
        this.writer = writer;
    }

    /**
     * Encodes and writes the provided {@link Json5Element} into json5 according to the specification
     * and the configured options. The element can be any json5 element. All child trees will be included.
     * This function writes with depth {@code 0} and expects a root element.
     * For nested elements, see the other write methods.
     * @param element Element to encode
     * @throws IOException If an I/O error occurs
     * @see #Json5Writer(Json5Options, Writer) Configuration options
     * @see #write(Json5Element, int)
     */
    public void write(Json5Element element) throws IOException {
        write(element, 0);
    }

    /**
     * Encodes and writes the provided {@link Json5Element} into json5 according to the specification
     * and the configured options. The element can be any json5 element. All child trees will be included.
     * @param element Element to encode
     * @param depth Depth of the current Json5 tree. Root node is {@code 0}. Counts {@code +1} on every child element.
     * @throws IOException If an I/O error occurs
     * @see #Json5Writer(Json5Options, Writer) Configuration options
     * @see #write(Json5Element)
     */
    public void write(Json5Element element, int depth) throws IOException {
        Objects.requireNonNull(element);

        if (element.isJson5Null()) {
            writeNull();
        } else if (element.isJson5Object()) {
            writeObject(element.getAsJson5Object(), depth);
        } else if (element.isJson5Array()) {
            writeArray(element.getAsJson5Array(), depth);
        } else if (element.isJson5Primitive()) {
            writePrimitive(element.getAsJson5Primitive());
        } else {
            throw new UnsupportedOperationException("Unknown json element with type class "
                    + element.getClass().getName());
        }
    }

    /**
     * Writes any associated comments for the provided {@link Json5Element}.
     * Checks if {@link Json5Options#isWriteComments()} is {@code true} and if the element has any comment assigned.
     * @param element Element target
     * @param depth Depth to use for writing
     */
    public void writeComment(Json5Element element, int depth) throws IOException {
        if (!options.isWriteComments() || !element.hasComment()) {
            return;
        }

        String indent = depthToIndent(depth);
        String comment = element.getComment();
        String[] lines = comment.split("\n");
        boolean multiLineComment = lines.length > 1;

        if (options.getIndentFactor() > 0) {
            // pretty-printing
            if (multiLineComment) {
                writer.append(indent).append("/*\n");
                for (String line : lines) {
                    writer.append(indent).append(" * ").append(line).append("\n");
                }
                writer.append(indent).append(" */\n");
            } else {
                writer.append(indent).append("// ").append(comment).append("\n");
            }
        } else {
            // write in the shortest possible style
            writer.append("/*");
            for (int i = 0; i < lines.length; i++) {
                writer.append(lines[i]);
                if (i != lines.length - 1) {
                    // Use whitespace for simulated break-lines
                    writer.append(" ");
                }
            }
            writer.append("*/");
        }
    }

    /**
     * Writes the equivalent of a {@link de.marhali.json5.Json5Null}({@code null}) value.
     * @throws IOException If an I/O error occurs.
     */
    public void writeNull() throws IOException {
        writer.append("null");
    }

    /**
     * Writes the provided primitive to the stream and encodes it if necessary.
     * @param primitive Primitive value.
     * @throws IOException If an I/O error occurs.
     */
    public void writePrimitive(Json5Primitive primitive) throws IOException {
        Objects.requireNonNull(primitive);

        if (primitive.isString()) {
            writer.append(quote(primitive.getAsString()));
        } else {
            writer.append(primitive.getAsString());
        }
    }

    /**
     * Writes the provided {@link Json5Object} to the stream.
     * @param object Object to encode
     * @param depth Depth to use for writing
     * @throws IOException If an I/O error occurs.
     * @see #write(Json5Element)
     */
    public void writeObject(Json5Object object, int depth) throws IOException {
        Objects.requireNonNull(object);

        if (depth == 0) {
            // Root element comment
            writeComment(object, depth);
        }

        int childDepth = depth + 1;
        String indent = depthToIndent(depth);
        String childIndent = depthToIndent(childDepth);

        writer.append('{');

        int index = -1;
        for (Map.Entry<String, Json5Element> entry : object.entrySet()) {
            index++;

            if (options.getIndentFactor() > 0) {
               writer.append("\n");
            }

            writeComment(entry.getValue(), childDepth);

            if (options.getIndentFactor() > 0)
                writer.append(childIndent);

            writer.append(quoteKey(entry.getKey()))
                .append(':');

            if (options.getIndentFactor() > 0)
                writer.append(' ');

            write(entry.getValue(), childDepth);

            if (options.isTrailingComma() || index < object.size() - 1) {
                writer.append(",");
            }
        }

        if (options.getIndentFactor() > 0)
            writer.append('\n').append(indent);

        writer.append('}');
    }

    /**
     * Writes the provided {@link Json5Array} to the stream.
     * @param array Array to encode
     * @param depth Depth to use for writing
     * @throws IOException If an I/O error occurs.
     * @see #write(Json5Element)
     */
    public void writeArray(Json5Array array, int depth) throws IOException {
        Objects.requireNonNull(array);

        if (depth == 0) {
            // Root element comment
            writeComment(array, depth);
        }

        int childDepth = depth + 1;
        String indent = depthToIndent(depth);
        String childIndent = depthToIndent(childDepth);

        writer.append('[');

        int index = -1;
        for (Json5Element value : array) {
            index++;

            if (options.getIndentFactor() > 0)
                writer.append('\n');

            writeComment(value, childDepth);

            writer.append(childIndent);

            write(value, childDepth);

            if (options.isTrailingComma() || index < array.size() - 1) {
                writer.append(",");
            }
        }

        if(options.getIndentFactor() > 0)
            writer.append('\n').append(indent);

        writer.append(']');
    }

    public String quoteKey(String key) {
        if (options.isQuoteless() && EcmaScriptIdentifier.isValid(key)) {
            return key;
        } else {
            return quote(key);
        }
    }

    public String quote(String string) {
        return quote(string, options);
    }

    /**
     * Quotes the provided string according to the json5 <a href="https://spec.json5.org/#strings">specification</a>.
     * @param string String to quote
     * @return quoted string
     */
    static String quote(String string, Json5Options options) {
        final char quote = options.isQuoteSingle() ? '\'' : '"';

        if (string == null || string.isEmpty())
            return String.valueOf(quote).repeat(2);

        StringBuilder quoted = new StringBuilder(string.length() + 2);
        boolean ascii = options.isStringifyAscii();

        quoted.append(quote);

        for (int i = 0, n = string.length(); i < n; ++i) {
            char c = string.charAt(i);

            if(c == quote) {
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
                    boolean unicode = false;

                    if (!ascii) {
                        // escape non-graphical characters (https://www.unicode.org/versions/Unicode13.0.0/ch02.pdf#G286941)
                        switch(Character.getType(c)) {
                            case Character.FORMAT:
                            case Character.LINE_SEPARATOR:
                            case Character.PARAGRAPH_SEPARATOR:
                            case Character.CONTROL:
                            case Character.PRIVATE_USE:
                            case Character.SURROGATE:
                            case Character.UNASSIGNED:
                                unicode = true;
                                break;
                            default:
                                break;
                        }
                    }
                    else unicode = c > 0x7F;

                    if(unicode) {
                        quoted.append("\\u");
                        quoted.append(String.format("%04X", (int) c));
                    }
                    else quoted.append(c);
            }
        }

        quoted.append(quote);

        return quoted.toString();
    }

    private String depthToIndent(int depth) {
        if (options.getIndentFactor() > 0) {
            return " ".repeat(depth * options.getIndentFactor());
        }

        return "";
    }
}

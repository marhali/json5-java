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

import de.marhali.json5.Json5Array;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import de.marhali.json5.exception.Json5Exception;

import java.util.Objects;

/**
 * A parser to parse tokenized Json5 data into a parse tree of {@link Json5Element}'s.
 *
 * @author Marcel Haßlinger
 * @author SyntaxError404
 */
public final class Json5Parser {

    private Json5Parser() {}

    /**
     * Parses the specified {@link Json5Lexer lexer} into a parse tree of {@link Json5Element}'s.
     * Thereby it does not matter if the provided root element is an array or object.
     * @param lexer Tokenized json5 data
     * @return a parse tree of {@link Json5Element}'s corresponding to the specified JSON5 or {@code null} if lexer does not provide any data
     */
    public static Json5Element parse(Json5Lexer lexer) {
        Objects.requireNonNull(lexer);

        switch (lexer.nextClean()) {
            case '{':
                lexer.back();
                return parseObject(lexer);
            case '[':
                lexer.back();
                return parseArray(lexer);
            case 0:
                return null;
            default:
                throw lexer.syntaxError("Unknown or unexpected control character");
        }
    }

    /**
     * Parses the specified {@link Json5Lexer lexer} into a parse tree of an {@link Json5Object}.
     * If the provided data does not correspond to a json object a {@link Json5Exception} will be thrown.
     * @param lexer Tokenized json5 data.
     * @return a parse tree of {@link Json5Object} corresponding to the specified JSON5.
     * @see #parse(Json5Lexer)
     */
    public static Json5Object parseObject(Json5Lexer lexer) {
        Objects.requireNonNull(lexer);

        if(lexer.nextClean() != '{') {
            throw lexer.syntaxError("A json object must begin with '{'");
        }

        Json5Object object = new Json5Object();

        char control;
        String key;

        while (true) {
            control = lexer.nextClean();
            switch (control) {
                case 0:
                    throw lexer.syntaxError("A json object must end with '}'");
                case '}':
                    return object;
                default:
                    lexer.back();
                    key = lexer.nextMemberName();
            }

            if(object.has(key)) {
                throw new Json5Exception("Duplicate key " + key);
            }

            if(lexer.nextClean() != ':') {
                throw lexer.syntaxError("Expected ':' after a key, got '" + control + "' instead");
            }

            object.add(key, lexer.nextValue());
            control = lexer.nextClean();

            if(control == '}') {
                return object;
            }

            if(control != ',') {
                throw lexer.syntaxError("Expected ',' or '}' after value, got '" + control + "' instead");
            }
        }
    }

    /**
     * Parses the specified {@link Json5Lexer lexer} into a parse tree of an {@link Json5Array}.
     * If the provided data does not correspond to a json array a {@link Json5Exception} will be thrown.
     * @param lexer Tokenized json5 data.
     * @return a parse tree of {@link Json5Array} corresponding to the specified JSON5.
     * @see #parse(Json5Lexer)
     */
    public static Json5Array parseArray(Json5Lexer lexer) {
        Objects.requireNonNull(lexer);

        if(lexer.nextClean() != '[') {
            throw lexer.syntaxError("A json array must begin with '['");
        }

        Json5Array array = new Json5Array();
        char control;

        while(true) {
            control = lexer.nextClean();
            switch (control) {
                case 0:
                    throw lexer.syntaxError("A json array must end with ']'");
                case ']':
                    return array;
                default:
                    lexer.back();
            }

            array.add(lexer.nextValue());
            control = lexer.nextClean();

            if(control == ']') {
                return array;
            }

            if(control != ',') {
                throw lexer.syntaxError("Expected ',' or ']' after value, got '" + control + "' instead");
            }
        }
    }
}

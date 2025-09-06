/*
 * Copyright (C) 2024 Ultreon Team
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Unit tests for comment handling in the {@link Json5} core class.
 *
 * @author Ultreon Team
 */
public class TestJson5Comments {
    private Json5 json5;

    private static Json5Object getJson5Object() {
        Json5Object root = new Json5Object();
        root.setComment("Root object comment");

        Json5Boolean enabled = new Json5Boolean(true);
        enabled.setComment("This is the enabled flag");
        root.add("enabled", enabled);

        Json5Array services = new Json5Array();

        Json5String serviceName = new Json5String("auth-service");
        serviceName.setComment("Authentication service");
        services.add(serviceName);

        root.add("services", services);
        root.setComment("services", "List of services");
        return root;
    }

    private InputStream getTestResource(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    private String getTestResourceContent(String fileName) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(getTestResource(fileName))) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            for (int result = bis.read(); result != -1; result = bis.read()) {
                buf.write((byte) result);
            }

            return Optional.ofNullable(buf.toString(StandardCharsets.UTF_8)).map(s -> s.replace("\r\n", "\n")).map(
                s -> s.replace("\r", "\n")).orElse(null);
        }
    }

    @BeforeEach
    void setup() {
        json5 = Json5.builder(
            builder -> builder.allowInvalidSurrogate().readComments().quoteSingle().indentFactor(2).build());
    }

    @Test
    void parseAndVerifyComments() throws IOException {
        try (InputStream stream = getTestResource("test.comments.json5")) {
            Json5Element element = json5.parse(stream);
            assertTrue(element.isJson5Object());

            Json5Object obj = element.getAsJson5Object();

            // Verify comments are read correctly
            assertEquals("The master switch for the feature", obj.get("enabled").getComment());
            assertEquals("A list of network ports", obj.get("ports").getComment());

            Json5Array ports = obj.getAsJson5Array("ports");
            assertEquals("Standard port", ports.get(0).getComment());
            assertEquals("Alternate port", ports.get(1).getComment());
            assertEquals("""
                         /**
                          * Admin port
                          * Do not expose publicly
                          */\
                         """, ports.get(2).getComment());

            Json5Object user = obj.getAsJson5Object("user");
            assertEquals("User configuration", user.getComment());
            assertEquals("The user's login name", user.get("name").getComment());
        }
    }

    @Test
    void serializeWithComments() throws IOException {
        try (InputStream stream = getTestResource("test.comments.json5")) {
            Json5Element element = json5.parse(stream);
            String expected = getTestResourceContent("expect.comments.json5");
            assertEquals(expected, json5.serialize(element));
        }
    }

    @Test
    void 少引号顶部注释测试() throws IOException {
        try (InputStream stream = getTestResource("expect.quoteless.comment.json5")) {
            Json5Options options =
                new Json5OptionsBuilder().quoteless().quoteSingle().readComments().prettyPrinting().build();
            Json5 json = new Json5(options);
            Json5Element element = json.parse(stream);
            String expected = getTestResourceContent("expect.quoteless.comment.json5");
            assertEquals(expected, json.serialize(element));
        }
    }

    @Test
    void 深拷贝与无注释拷贝测试() throws IOException {
        try (InputStream stream = getTestResource("expect.quoteless.comment.json5")) {
            Json5Options options =
                new Json5OptionsBuilder().quoteless().quoteSingle().readComments().prettyPrinting().build();
            Json5 json = new Json5(options);
            Json5Element element = json.parse(stream);
            assertEquals(getTestResourceContent("expect.quoteless.comment.json5"), json.serialize(element.deepCopy()));
            assertEquals(getTestResourceContent("expect.无注释拷贝.json5"), json.serialize(element.noCommentCopy()));
        }
    }

    @Test
    void programmaticallyAddComments() throws IOException {
        Json5Object root = getJson5Object();

        String expected = """
                          // Root object comment
                          {
                            // This is the enabled flag
                            'enabled': true,
                            // List of services
                            'services': [
                              // Authentication service
                              'auth-service'
                            ]
                          }""";
        assertEquals(expected, json5.serialize(root));
        assertEquals("""
                     {"enabled":true,"services":["auth-service"]}\
                     """, root.toStandardString());
    }

    @Test
    void testCopyCommentToVariants() {
        // 基础元素注释复制
        Json5String sourceStr = new Json5String("hello");
        sourceStr.setComment("这是一个字符串注释");

        Json5String targetStr = new Json5String("hello");
        sourceStr.copyCommentTo(targetStr);

        assertEquals("这是一个字符串注释", targetStr.getComment(), "字符串注释应被复制");

        // 数组注释复制（包括元素注释）
        Json5Element sourceArr = json5.parse("""
                                             // 数组注释
                                             [// 注释A
                                             'a']
                                             """);

        Json5Array targetArr = new Json5Array();
        targetArr.add(new Json5String("a"));
        targetArr.add(new Json5String("b"));

        sourceArr.getAsJson5Array().mergeCommentTo(targetArr);

        assertEquals("数组注释", targetArr.getComment(), "数组注释应被复制");
        assertEquals("注释A", targetArr.get(0).getComment(), "元素0注释应被复制");

        // 对象注释复制（键注释）
        Json5Object sourceObj = json5.parse("""
                                            // 对象注释
                                            {
                                            // 注释X
                                            x:1,
                                            // 注释Y
                                            y:2
                                            }
                                            """).getAsJson5Object();
        Json5Object targetObj = new Json5Object();
        targetObj.add("x", new Json5Number(1));
        targetObj.add("y", new Json5Number(2));

        sourceObj.mergeCommentTo(targetObj);

        assertEquals("对象注释", targetObj.getComment(), "对象注释应被复制");
        assertEquals("注释X", targetObj.getComment("x"), "键x注释应被复制");
        assertEquals("注释Y", targetObj.getComment("y"), "键y注释应被复制");
    }
}

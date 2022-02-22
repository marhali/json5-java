# json5-java 
[![Build](https://img.shields.io/github/workflow/status/marhali/json5-java/Maven%20CD)](https://github.com/marhali/json5-java/actions)
[![JavaDoc](https://javadoc.io/badge2/de.marhali/json5-java/javadoc.svg)](https://javadoc.io/doc/de.marhali/json5-java)
[![Coverage](https://img.shields.io/codecov/c/github/marhali/json5-java)](https://codecov.io/gh/marhali/json5-java)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://paypal.me/marhalide)

This is a reference implementation of the [JSON5 standard](https://json5.org/) in Java 11+, 
capable of parsing and serialization of JSON5 data.

This library is an enhanced version of [Synt4xErr0r4 / json5](https://github.com/Synt4xErr0r4/json5), 
which provides a better full-fledged API inspired by the [GSON](https://github.com/google/gson) library.

## Download
Download the [latest release](https://github.com/marhali/json5-java/releases/latest) manually or add a Maven dependency. 
Don't worry the project is already in the Maven Central Repository. Just add the following dependency:
```xml
<dependencies>
    <dependency>
        <groupId>de.marhali</groupId>
        <artifactId>json5-java</artifactId>
    </dependency>
</dependencies>
```

## Usage
This library can be used by either configuring a [Json5](src/main/java/de/marhali/json5/Json5.java) 
instance or by using the underlying [Json5Parser](src/main/java/de/marhali/json5/stream/Json5Parser.java) 
and [Json5Writer](src/main/java/de/marhali/json5/stream/Json5Writer.java).

The following section describes how to use this library with the 
[Json5](src/main/java/de/marhali/json5/Json5.java) core class.

### Configure Json5 instance
See [Parsing & Serialization Options](#parsing--serialization-options) to see a list of possible configuration options.
```java
// Using builder pattern
Json5 json5 = Json5.builder(options ->
        options.allowInvalidSurrogate().quoteSingle().prettyPrinting().build());

// Using configuration object
Json5Options options = new Json5Options(true, true, true, 2);
Json5 json5 = new Json5(options);
```

### Parsing 
```java
Json5 json5 = ...

// Parse from a String literal
Json5Element element = 
        json5.parse("{ 'key': 'value', 'array': ['first val','second val'] }");

// ...

// Parse from a Reader or InputStream
try(InputStream stream = ...) {
    Json5Element element = json5.parse(stream);
    // ...
} catch (IOException e) {
    // ...
}
```

### Serialization
```java
Json5Element element = ...

// Serialize to a String literal
String jsonString = json5.serialize(element);

// ...

// Serialize to a Writer or OutputStream        
try(OutputStream stream = ...) {
    json5.serialize(element, stream);
    // ...
} catch (IOException e) {
    // ...
}
```

## Documentation
Detailed javadoc documentation can be found at [javadoc.io](https://javadoc.io/doc/de.marhali/json5-java).

### Parsing & Serialization Options
This library supports a few customizations to adjust the behaviour of parsing and serialization.
For a detailed explanation see the [Json5Options](src/main/java/de/marhali/json5/Json5Options.java) class.

- allowInvalidSurrogates
- quoteSingle
- trailingComma
- indentFactor

## License
This library is released under the [Apache 2.0 license](LICENSE).

Partial parts of the project are based on [GSON](https://github.com/google/gson) and [Synt4xErr0r4 / json5](https://github.com/Synt4xErr0r4/json5). The affected classes contain the respective license notice. 
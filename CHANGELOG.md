# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2022-02-21
### Added
- Object-oriented access to all Json5 types
- Parse json5 data by InputStream / Reader / String
- Serialize json5 data to OutputStream / Writer / String
- Json5Options with builder pattern (Json5OptionsBuilder) to configure Json5
  - options: allowInvalidSurrogates, quoteSingle, trailingComma, indentFactor
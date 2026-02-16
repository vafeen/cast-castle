# Cast Castle [![GitHub Tag](https://img.shields.io/github/v/tag/vafeen/Cast-Castle)](https://github.com/vafeen/Cast-Castle/releases/latest/)

<div align="center">
  <img src="pictures/ico.png" alt="CastCastle Logo" width="600">
</div>

**Cast Castle** is a lightweight Kotlin library for generating mapping code between `data class`
using **KSP (Kotlin Symbol Processing)**.  
It automatically creates separate extension mapping functions based on the
`@CastCastleMapper` annotation for classes or standalone functions.

## Implementation

[![GitHub Tag](https://img.shields.io/github/v/tag/vafeen/Cast-Castle)](https://github.com/vafeen/Cast-Castle/releases/latest/)

Gradle:
```kotlin
implementation("io.github.vafeen:cast-castle-annotations:VERSION")
ksp("io.github.vafeen:cast-castle-processor:VERSION")
```

Other:

https://central.sonatype.com/artifact/io.github.vafeen/cast-castle-annotations
https://central.sonatype.com/artifact/io.github.vafeen/cast-castle-processor

## Docs

[DOCUMENTATION](DOCUMENTATION.md)

[SAMPLES](SAMPLES.md)

# Documentation

Annotate with @CastCastleMapper any class, and the set of extension functions for this class will be
generated

If your classes have different parameters, it will be added to generated funcs, for example:

## Classes/Interfaces/Objects

In Classes/Interfaces/Objects lib generate implementation only abstract functions, and if you want
to have an implementation for func with body, annotate it too:

Source code:

```kotlin
data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
class AdditionalFieldsMapper {
    @CastCastleMapper // additional annotation for funcs with body for forced generation
    fun map(a: A): B = mapCastCastle(a, 1)

    @CastCastleMapper
    fun map(b: B): A = mapCastCastle(b, 1)
}
```

Generated code:

```kotlin
public fun AdditionalFieldsMapper.mapCastCastle(
    a: ru.vafeen.samples.sample3.kotlin.A,
    z: kotlin.Int
): ru.vafeen.samples.sample3.kotlin.B {
    return ru.vafeen.samples.sample3.kotlin.B(
        x = a.x,
        z = z
    )
}

public fun AdditionalFieldsMapper.mapCastCastle(
    b: ru.vafeen.samples.sample3.kotlin.B,
    y: kotlin.Int
): ru.vafeen.samples.sample3.kotlin.A {
    return ru.vafeen.samples.sample3.kotlin.A(
        x = b.x,
        y = y
    )
}

```

## Standalone functions

Source code:

```kotlin
data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
fun standaloneMapper1(a: A): B = /* standaloneMapper1CastCastle(a, 1) */

    @CastCastleMapper
    fun B.standaloneMapper2(): A = /* standaloneMapper2CastCastle(1) */ 
```

Generated code:

```kotlin
public fun standaloneMapper1CastCastle(
    a: ru.vafeen.samples.sample3.kotlin.A,
    z: kotlin.Int
): ru.vafeen.samples.sample3.kotlin.B {
    return ru.vafeen.samples.sample3.kotlin.B(
        x = a.x,
        z = z
    )
}

public fun ru.vafeen.samples.sample3.kotlin.B.standaloneMapper2CastCastle(y: kotlin.Int): ru.vafeen.samples.sample3.kotlin.A {
    return ru.vafeen.samples.sample3.kotlin.A(
        x = this.x,
        y = y
    )
}
```

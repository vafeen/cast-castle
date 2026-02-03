package ru.vafeen.samples.sample3

import ru.vafeen.castcastle.annotations.CastCastleMapper


data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
interface AdditionalFieldsMapper {
    @CastCastleMapper
    fun map(a: A): B = mapCastCastle(a, 1)
    @CastCastleMapper
    fun map(b: B): A = mapCastCastle(b, 1)
}
package ru.vafeen.samples.sample3.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper


data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
fun standaloneMapper1(a: A): B = standaloneMapper1CastCastle(a, 1)

@CastCastleMapper
fun B.standaloneMapper2(): A = standaloneMapper2CastCastle(1)

@CastCastleMapper
class AdditionalFieldsMapper {
    @CastCastleMapper
    fun mapA(a: A): B = mapACastCastle(a, 1)

    @CastCastleMapper
    fun mapB(b: B): A = mapBCastCastle(b, 1)
}
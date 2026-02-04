package ru.vafeen.samples.sample4.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
class CompanionObjectFuncs {
    @CastCastleMapper
    companion object {
        @CastCastleMapper
        fun companionObjectFuncsMapper(a: A): B = companionObjectFuncsMapperCastCastle(a, 1)
    }
}
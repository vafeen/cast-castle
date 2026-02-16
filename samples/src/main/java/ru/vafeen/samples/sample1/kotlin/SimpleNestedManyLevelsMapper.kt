package ru.vafeen.samples.sample1.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class A(val inner1Level1: InnerLevel1A, val inner2Level1: InnerLevel1A, val someInt: Int)
data class B(val inner1Level1: InnerLevel1B, val inner2Level1: InnerLevel1B)

data class InnerLevel1A(val inner1Level2: InnerLevel2A, val inner2Level2: InnerLevel2A)
data class InnerLevel1B(val inner1Level2: InnerLevel2B, val inner2Level2: InnerLevel2B)

data class InnerLevel2A(val inner1Level3: InnerLevel3A, val inner2Level3: InnerLevel3A)
data class InnerLevel2B(val inner1Level3: InnerLevel3B, val inner2Level3: InnerLevel3B)

data class InnerLevel3A(val first: Int, val second: String)
data class InnerLevel3B(val first: Int, val second: String)

@CastCastleMapper
interface SimpleNestedThreeLevelsMapper {
    @CastCastleMapper
    fun map(a: A): B = mapCastCastle(a)

    @CastCastleMapper
    fun map(b: B): A = mapCastCastle(b, 1)

//    fun mapLevel1(inner1Level1: InnerLevel1A): InnerLevel1B
//    fun mapLevel1(inner1Level1: InnerLevel1B): InnerLevel1A
//    fun mapLevel2(innerLevel2A: InnerLevel2A): InnerLevel2B
//    fun mapLevel2(innerLevel2A: InnerLevel2B): InnerLevel2A
//    fun mapLevel3(inner1Level3: InnerLevel3A): InnerLevel3B
//    fun mapLevel3(inner1Level3: InnerLevel3B): InnerLevel3A
}
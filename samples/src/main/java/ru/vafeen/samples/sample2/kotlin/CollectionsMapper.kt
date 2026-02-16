package ru.vafeen.samples.sample2.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class A(val inner1Level1: List<InnerLevel1A>, val inner2Level1: InnerLevel1A)
data class B(val inner1Level1: List<InnerLevel1B>, val inner2Level1: InnerLevel1B)
data class InnerLevel1A(val inner1Level2: List<InnerLevel2A>, val inner2Level2: InnerLevel2A)
data class InnerLevel1B(val inner1Level2: List<InnerLevel2B>, val inner2Level2: InnerLevel2B)
data class InnerLevel2A(val x: String, val y: Int)
data class InnerLevel2B(val x: Int, val y: String)

@CastCastleMapper
internal interface CollectionsMapper {
    @CastCastleMapper
    fun mapA(a: A): B = mapACastCastle(a)

    @CastCastleMapper
    fun mapB(b: B): A = mapBCastCastle(b)

    fun string(string: String): Int = string.toInt()
    fun int(int: Int): String = "$int"

    @CastCastleMapper
    fun mapLevel1A(inner1Level1: InnerLevel1A): InnerLevel1B = mapLevel1ACastCastle(inner1Level1)

    @CastCastleMapper
    fun mapLevel1B(inner1Level1: InnerLevel1B): InnerLevel1A = mapLevel1BCastCastle(inner1Level1)

    //    fun mapLevel1A(inner1Level1: List<InnerLevel1A>): List<InnerLevel1B>
    //    fun mapLevel1B(inner1Level1: List<InnerLevel1B>): List<InnerLevel1A>
    //    fun mapLevel2A(innerLevel2A: InnerLevel2A): InnerLevel2B
    //    fun mapLevel2B(innerLevel2A: InnerLevel2B): InnerLevel2A
    //    fun mapLevel2A(innerLevel2A: List<InnerLevel2A>): List<InnerLevel2B>
    //    fun mapLevel2B(innerLevel2A: List<InnerLevel2B>): List<InnerLevel2A>
}
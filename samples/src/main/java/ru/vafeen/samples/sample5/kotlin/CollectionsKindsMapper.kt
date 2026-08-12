package ru.vafeen.samples.sample5.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class InnerA(val x: Int, val name: String)
data class InnerB(val x: Int, val name: String)

data class SetHolderA(val inner: Set<InnerA>, val mixed: List<InnerA>)
data class SetHolderB(val inner: Set<InnerB>, val mixed: Set<InnerB>)

data class NestedListA(val matrix: List<List<InnerA>>)
data class NestedListB(val matrix: List<List<InnerB>>)

@CastCastleMapper
class CollectionsKindsMapper {
    @CastCastleMapper
    fun mapSetHolder(setHolderA: SetHolderA): SetHolderB = mapSetHolderCastCastle(setHolderA)

    @CastCastleMapper
    fun mapNested(nestedListA: NestedListA): NestedListB = mapNestedCastCastle(nestedListA)
}

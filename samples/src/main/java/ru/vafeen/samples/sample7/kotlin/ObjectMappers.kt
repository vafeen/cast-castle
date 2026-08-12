package ru.vafeen.samples.sample7.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class ItemA(val sku: String, val price: Int)
data class ItemB(val sku: String, val price: Int)

data class Order(val id: Long, val items: List<ItemA>)
data class OrderResponse(val id: Long, val items: List<ItemB>)

@CastCastleMapper
object OrderMapper {
    @CastCastleMapper
    fun mapOrder(order: Order): OrderResponse = mapOrderCastCastle(order)

    @CastCastleMapper
    fun mapItem(item: ItemA): ItemB = mapItemCastCastle(item)
}

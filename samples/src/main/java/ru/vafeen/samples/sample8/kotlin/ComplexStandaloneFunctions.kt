package ru.vafeen.samples.sample8.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class AddressA(val street: String, val city: String, val zip: Int)
data class AddressB(val street: String, val city: String, val zip: Int)

data class ContactA(val name: String, val addresses: List<AddressA>)
data class ContactB(val name: String, val addresses: List<AddressB>, val nickname: String)

@CastCastleMapper
fun mapContact(contactA: ContactA): ContactB = mapContactCastCastle(contactA, "n/a")

@CastCastleMapper
internal fun copyAddress(addressA: AddressA): AddressB = copyAddressCastCastle(addressA)

@CastCastleMapper
fun AddressA.standaloneAddressToB(): AddressB = standaloneAddressToBCastCastle()

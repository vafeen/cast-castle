package ru.vafeen.samples.sample6.kotlin

import ru.vafeen.castcastle.annotations.CastCastleMapper

data class Device(val id: Int, val model: String)
data class DeviceResponse(val id: Int, val model: String)

data class Firmware(val version: String, val channel: String)
data class FirmwareResponse(val version: String, val channel: String)

@CastCastleMapper
interface AbstractDeviceMapper {
    fun mapDevice(device: Device): DeviceResponse
    fun mapFirmware(firmware: Firmware): FirmwareResponse
}

class DeviceMapperImpl : AbstractDeviceMapper {
    override fun mapDevice(device: Device): DeviceResponse = mapDeviceCastCastle(device)
    override fun mapFirmware(firmware: Firmware): FirmwareResponse = mapFirmwareCastCastle(firmware)
}

package ru.vafeen.castcastle.processor.processing.utils

import ru.vafeen.castcastle.processor.processing.models.ClassModel
import ru.vafeen.castcastle.processor.processing.models.MapperClass

private fun ClassModel.fullName() = "$packageName.$name"

internal fun ClassModel.fullNameWithGenerics(): String {
    return if (typeArguments.isNotEmpty()) {
        val genericParams = typeArguments.joinToString(", ") { it.fullName() }
        "${fullName()}<$genericParams>"
    } else {
        fullName()
    }
}

internal fun MapperClass.fullName() = "$packageName.$name"
package ru.vafeen.castcastle.processor.processing.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import ru.vafeen.castcastle.processor.processing.models.ClassModel
import ru.vafeen.castcastle.processor.processing.models.MapperClass

private fun ClassModel.fullName() = "$packageName.$name"

internal fun ClassModel.fullNameWithGenerics(): String {
    return if (typeArguments.isNotEmpty()) {
        val genericParams = typeArguments.joinToString(", ") { it.fullNameWithGenerics() }
        "${fullName()}<$genericParams>"
    } else {
        fullName()
    }
}

internal fun ClassModel.toTypeName(): TypeName {
    val names = name.split(".")
    val className = ClassName(packageName, names.first(), *names.drop(1).toTypedArray())
    return if (typeArguments.isNotEmpty()) {
        className.parameterizedBy(typeArguments.map { it.toTypeName() })
    } else {
        className
    }
}

internal fun MapperClass.toClassName(): ClassName {
    val names = name.split(".")
    return ClassName(packageName, names.first(), *names.drop(1).toTypedArray())
}
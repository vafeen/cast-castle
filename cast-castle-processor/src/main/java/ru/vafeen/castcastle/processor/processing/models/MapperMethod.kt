package ru.vafeen.castcastle.processor.processing.models

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal data class MapperMethod(
    val name: String,
    val sourceParameter: Parameter,
    val targetClass: ClassModel,
    val isAbstract: Boolean,
    val isMapperAnnotated: Boolean,
    val kspDeclaration: KSFunctionDeclaration
)
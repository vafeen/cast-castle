package ru.vafeen.castcastle.processor.processing.models

import com.google.devtools.ksp.symbol.KSFunctionDeclaration

internal data class ImplMapperMethod(
    val name: String,
    val from: Parameter,
    val to: ClassModel,
    val baseMethod: KSFunctionDeclaration,
)

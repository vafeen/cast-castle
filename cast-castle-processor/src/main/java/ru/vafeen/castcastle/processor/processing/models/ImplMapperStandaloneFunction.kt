package ru.vafeen.castcastle.processor.processing.models

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import ru.vafeen.castcastle.processor.processing.ProcessingVisibility

internal data class ImplMapperStandaloneFunction(
    val packageName: String,
    val name: String,
    val from: Parameter,
    val to: ClassModel,
    val visibility: ProcessingVisibility,
    val declaration: KSFunctionDeclaration,
    val isExtension: Boolean
)
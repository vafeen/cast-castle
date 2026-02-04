package ru.vafeen.castcastle.processor.processing.models

import com.google.devtools.ksp.symbol.KSFile
import ru.vafeen.castcastle.processor.processing.ProcessingVisibility

internal data class ClassModel(
    val name: String,
    val packageName: String,
    val thisClass: KSFile?,
    val visibility: ProcessingVisibility,
    val parameters: List<Parameter>,
    val typeArguments: List<ClassModel> = emptyList()
)
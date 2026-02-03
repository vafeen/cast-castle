package ru.vafeen.castcastle.processor.processing.models

import com.google.devtools.ksp.symbol.KSFile
import ru.vafeen.castcastle.processor.processing.ProcessingVisibility

internal data class MapperClass(
    val name: String,
    val packageName: String,
    val thisClass: KSFile?,
    val visibility: ProcessingVisibility,
    val mappers: List<MapperMethod>,
    val isJava: Boolean,
)
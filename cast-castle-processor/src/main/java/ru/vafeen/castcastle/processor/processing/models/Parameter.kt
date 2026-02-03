package ru.vafeen.castcastle.processor.processing.models

internal interface FuncParameter {
    val name: String
    val classModel: ClassModel
}

internal data class Parameter(
    override val name: String,
    override val classModel: ClassModel,
    val hasDefault: Boolean,
) : FuncParameter

internal data class Receiver(
    override val name: String,
    override val classModel: ClassModel,
) : FuncParameter
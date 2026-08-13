package ru.vafeen.castcastle.processor.processing

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import ru.vafeen.castcastle.processor.processing.models.ImplMapperClass
import ru.vafeen.castcastle.processor.processing.models.ImplMapperStandaloneFunction


internal class FileWriter(private val codeGenerator: CodeGenerator) {
    fun writeClass(
        implMapperClass: ImplMapperClass,
        fileSpec: FileSpec
    ) {
        val parent = implMapperClass.parent
        writeFile(
            fileSpec = fileSpec,
            dependencies = Dependencies(
                aggregating = false,
                sources = if (parent != null) arrayOf(parent) else arrayOf()
            )
        )
    }

    fun writeStandaloneFunctions(
        implMapperStandaloneFunctions: List<ImplMapperStandaloneFunction>,
        fileSpec: FileSpec
    ) {
        writeFile(
            fileSpec = fileSpec,
            dependencies = Dependencies(
                aggregating = false,
                sources = implMapperStandaloneFunctions.map { it.declaration.containingFile as KSFile }
                    .toTypedArray()
            )
        )
    }

    private fun writeFile(fileSpec: FileSpec, dependencies: Dependencies) {
        codeGenerator.createNewFile(dependencies, fileSpec.packageName, fileSpec.name)
            .writer()
            .use { out ->
                fileSpec.writeTo(out)
            }
    }
}

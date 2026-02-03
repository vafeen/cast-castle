package ru.vafeen.castcastle.processor.processing

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import ru.vafeen.castcastle.processor.processing.models.ImplMapperClass
import ru.vafeen.castcastle.processor.processing.models.ImplMapperStandaloneFunction


internal class FileWriter(private val codeGenerator: CodeGenerator) {
    fun writeClass(
        implMapperClass: ImplMapperClass,
        classView: () -> String
    ) {
        val parent = implMapperClass.parent
        codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = if (parent != null) arrayOf(parent) else arrayOf()
            ),
            packageName = implMapperClass.packageName,
            fileName = implMapperClass.name
        )
            .writer()
            .use { out ->
                out.write(classView())
            }
    }

    fun writeStandaloneFunctions(
        packageName: String,
        fileName: String,
        implMapperStandaloneFunctions: List<ImplMapperStandaloneFunction>,
        funsView: () -> String
    ) {
        codeGenerator.createNewFile(
            dependencies = Dependencies(
                aggregating = false,
                sources = implMapperStandaloneFunctions.map { it.declaration.containingFile as KSFile }
                    .toTypedArray()
            ),
            packageName = packageName,
            fileName = fileName
        )
            .writer()
            .use { out ->
                out.write(funsView())
            }
    }

}

package ru.vafeen.castcastle.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import ru.vafeen.castcastle.processor.processing.ComponentsResolver
import ru.vafeen.castcastle.processor.processing.FileWriter
import ru.vafeen.castcastle.processor.processing.mapper_generators.KotlinPoetSpecGenerator
import ru.vafeen.castcastle.processor.processing.utils.toClassName
import ru.vafeen.castcastle.processor.processing.utils.toImplClassModel
import ru.vafeen.castcastle.processor.processing.utils.toImplMapperStandaloneFunction

internal var logger: KSPLogger? = null
internal val libName = "CastCastle"

internal class CastCastleProcessor private constructor(codeGenerator: CodeGenerator) :
    SymbolProcessor {

    constructor(
        codeGenerator: CodeGenerator,
        kspLogger: KSPLogger
    ) : this(codeGenerator) {
        logger = kspLogger
    }


    private val fileWriter = FileWriter(codeGenerator)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger?.info("This is ${this::class.simpleName}")
        val componentsResolver = ComponentsResolver(resolver)
        val interfaces = componentsResolver.getMapperInterfaces()
        val standaloneFunctions = componentsResolver.getMapperStandaloneFunctions()

        interfaces.forEach {
            val implMapperClass = it.toImplClassModel()
            val mappersForThisClass = componentsResolver.getAllMappersForThisInterface(it)
            val kotlinPoetSpecGenerator = KotlinPoetSpecGenerator(mappersForThisClass)
            val fileSpec = kotlinPoetSpecGenerator.generateFuncsForMapperClass(
                baseClassType = it.toClassName(),
                implMapperClass = implMapperClass
            )
            fileWriter.writeClass(
                implMapperClass = implMapperClass,
                fileSpec = fileSpec
            )
        }


        standaloneFunctions
            .groupBy { it.packageName }
            .forEach {
                val packageName = it.key
                val funcsInThisPackage =
                    it.value.map { func -> func.toImplMapperStandaloneFunction() }
                val kotlinPoetSpecGenerator = KotlinPoetSpecGenerator(listOf())
                val fileSpec = kotlinPoetSpecGenerator.generateStandaloneFunctions(
                    packageName = packageName,
                    implMapperStandaloneFunctions = funcsInThisPackage
                )

                fileWriter.writeStandaloneFunctions(
                    implMapperStandaloneFunctions = funcsInThisPackage,
                    fileSpec = fileSpec
                )
            }
        return emptyList()
    }

}
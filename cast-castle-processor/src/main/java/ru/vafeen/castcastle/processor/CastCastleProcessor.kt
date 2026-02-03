package ru.vafeen.castcastle.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import ru.vafeen.castcastle.processor.processing.ComponentsResolver
import ru.vafeen.castcastle.processor.processing.FileWriter
import ru.vafeen.castcastle.processor.processing.mapper_generators.StringViewGenerator
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
            fileWriter.writeClass(implMapperClass) {
                val mappersForThisClass = componentsResolver.getAllMappersForThisInterface(it)
                val stringViewGenerator = StringViewGenerator(mappersForThisClass)
                stringViewGenerator.generateFuncsForMapperClass(
                    baseClassType = it.name,
                    implMapperClass = implMapperClass
                )
            }
        }


        standaloneFunctions
            .groupBy { it.packageName }
            .forEach {
                val packageName = it.key
                val funcsInThisPackage =
                    it.value.map { func -> func.toImplMapperStandaloneFunction() }
                val stringViewGenerator = StringViewGenerator(listOf())

                fileWriter.writeStandaloneFunctions(
                    packageName = packageName,
                    implMapperStandaloneFunctions = funcsInThisPackage,
                    fileName = STANDALONE_FUNCTIONS_FILENAME
                ) {
                    stringViewGenerator.generateStandaloneFunctions(
                        packageName = packageName,
                        funcsInThisPackage
                    )
                }
            }
        return emptyList()
    }

    companion object {
        private const val STANDALONE_FUNCTIONS_FILENAME = "StandaloneFunctions"
    }

}
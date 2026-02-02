package ru.vafeen.castcastle.processor.processing.mapper_generators

import ru.vafeen.castcastle.processor.logger
import ru.vafeen.castcastle.processor.processing.models.ClassModel
import ru.vafeen.castcastle.processor.processing.models.ImplMapperClass
import ru.vafeen.castcastle.processor.processing.models.ImplMapperMethod
import ru.vafeen.castcastle.processor.processing.models.MapperMethod
import ru.vafeen.castcastle.processor.processing.models.Parameter
import ru.vafeen.castcastle.processor.processing.utils.copyright
import ru.vafeen.castcastle.processor.processing.utils.fullName
import ru.vafeen.castcastle.processor.processing.utils.fullNameWithGenerics
import ru.vafeen.castcastle.processor.processing.utils.getCollectionElementType
import ru.vafeen.castcastle.processor.processing.utils.isCollectionType
import java.time.LocalDateTime

internal class StringViewGenerator(private val mappers: List<MapperMethod>) {
    fun generateImplMapperClass(implMapperClass: ImplMapperClass): String {
        require(!isClassGenerationCalled) { "${StringViewGenerator::class.simpleName} must be called once for every implementation" }
        isClassGenerationCalled = true

        return buildString {
            appendLine("package ${implMapperClass.packageName}\n")
            appendLine("//updated: ${LocalDateTime.now()}\n")
            appendLine(copyright())
            appendLine("${implMapperClass.visibility.nameForFile()} class ${implMapperClass.name} : ${implMapperClass.parentInterfaceName} {")
            appendLine(
                implMapperClass.implMethods.joinToString(separator = "\n\n") {
                    generateImplMethod(it, implMapperClass.isJava)
                }.addIndent()
            )
            appendLine("}")
        }
    }

    fun generateImplMethod(implMapperMethod: ImplMapperMethod, isJava: Boolean): String =
        buildString {
            val returnTypeName = if (implMapperMethod.to.isCollectionType()) {
                implMapperMethod.to.fullNameWithGenerics()
            } else {
                implMapperMethod.to.fullName()
            }

            appendLine(
                "override fun ${implMapperMethod.name}" + "(${implMapperMethod.from.name}: ${implMapperMethod.from.classModel.fullNameWithGenerics()})" + ": $returnTypeName {"
            )
            appendLine(
                "return ${
                    recursiveGenerateMapperCall(
                        sourceVar = implMapperMethod.from.name,
                        sourceModel = implMapperMethod.from.classModel,
                        targetModel = implMapperMethod.to,
                        visitedTypes = mutableSetOf(),
                        currentMapperMethod = implMapperMethod,
                        isJava = isJava
                    )
                }".addIndent()
            )
            appendLine("}")
        }

    private var isClassGenerationCalled = false
    private var counter = 0
    private fun getReceiver() = "it${counter++}"
    private fun recursiveGenerateMapperCall(
        sourceVar: String,
        sourceModel: ClassModel,
        targetModel: ClassModel,
        visitedTypes: MutableSet<String>,
        currentMapperMethod: ImplMapperMethod,
        isJava: Boolean,
    ): String {
        val typeKey = "${sourceModel.fullNameWithGenerics()}->${targetModel.fullNameWithGenerics()}"
        if (typeKey in visitedTypes) {
            return "TODO(\"Circular mapping detected: ${sourceModel.fullNameWithGenerics()} -> ${targetModel.fullNameWithGenerics()}\")"
        }
        visitedTypes.add(typeKey)

        if (sourceModel.isCollectionType() && targetModel.isCollectionType()) {
            return generateCollectionMapping(
                sourceVar = sourceVar,
                sourceElementType = sourceModel.getCollectionElementType(),
                targetElementType = targetModel.getCollectionElementType(),
                targetCollectionFullType = targetModel.fullNameWithGenerics(),
                visitedTypes = visitedTypes,
                currentMapperMethod = currentMapperMethod,
                isJava = isJava
            )
        }

        val directMapper = findDirectMapper(sourceModel, targetModel, currentMapperMethod)

        return if (directMapper != null) {
            "${directMapper.name}($sourceVar)"
        } else {
            generateConstructorCall(
                sourceVar, sourceModel, targetModel, visitedTypes, currentMapperMethod, isJava
            )
        }
    }

    private fun generateConstructorCall(
        sourceVar: String,
        sourceModel: ClassModel,
        targetModel: ClassModel,
        visitedTypes: MutableSet<String>,
        currentMapperMethod: ImplMapperMethod,
        isJava: Boolean
    ): String = buildString {
        val targetTypeName = if (targetModel.typeArguments.isNotEmpty()) {
            targetModel.fullNameWithGenerics()
        } else {
            targetModel.fullName()
        }

        appendLine("$targetTypeName(")
        targetModel.parameters.forEach { targetParam ->
            val sourceParam = findMatchingSourceParameter(targetParam, sourceModel)

            val paramCall = sourceParam?.let { param ->
                val sourceFieldAccess = "$sourceVar.${param.name}"
                generateParameterMapping(
                    sourceFieldAccess = sourceFieldAccess,
                    sourceParam = param,
                    targetParam = targetParam,
                    visitedTypes = visitedTypes,
                    currentMapperMethod = currentMapperMethod,
                    isJava = isJava
                )
            } ?: run {
                logger?.error(
                    "No value with the same name provided for ${targetParam.name}",
                    currentMapperMethod.baseMethod
                )
                "TODO(\"Provide value for ${targetParam.name}\")"
            }

            appendLine("${if (!isJava) "${targetParam.name} = " else ""}$paramCall,".addIndent())
        }
        append(")")
    }

    private fun generateParameterMapping(
        sourceFieldAccess: String,
        sourceParam: Parameter,
        targetParam: Parameter,
        visitedTypes: MutableSet<String>,
        currentMapperMethod: ImplMapperMethod,
        isJava: Boolean
    ): String {
        return when {
            sourceParam.classModel.isCollectionType() && targetParam.classModel.isCollectionType() -> {
                generateCollectionMapping(
                    sourceVar = sourceFieldAccess,
                    sourceElementType = sourceParam.classModel.getCollectionElementType(),
                    targetElementType = targetParam.classModel.getCollectionElementType(),
                    targetCollectionFullType = targetParam.classModel.fullNameWithGenerics(),
                    visitedTypes = visitedTypes,
                    currentMapperMethod = currentMapperMethod,
                    isJava = isJava
                )
            }

            sourceParam.classModel.fullNameWithGenerics() == targetParam.classModel.fullNameWithGenerics() -> sourceFieldAccess


            else -> {
                recursiveGenerateMapperCall(
                    sourceVar = sourceFieldAccess,
                    sourceModel = sourceParam.classModel,
                    targetModel = targetParam.classModel,
                    visitedTypes = visitedTypes.toMutableSet(),
                    currentMapperMethod = currentMapperMethod,
                    isJava = isJava
                )
            }
        }
    }

    private fun generateCollectionMapping(
        sourceVar: String,
        sourceElementType: ClassModel?,
        targetElementType: ClassModel?,
        targetCollectionFullType: String,
        visitedTypes: MutableSet<String>,
        currentMapperMethod: ImplMapperMethod,
        isJava: Boolean
    ): String {
        if (sourceElementType == null || targetElementType == null) {
            logger?.warn(
                "Cannot determine collection element types for mapping",
                currentMapperMethod.baseMethod
            )
            return "$sourceVar // TODO: Add explicit mapper for collection types"
        }

        val elementMapper =
            findDirectMapper(sourceElementType, targetElementType, currentMapperMethod)
        val receiver = getReceiver()
        val collectionInitializer = getCollectionInitializer(targetCollectionFullType)

        val addContent = when {
            elementMapper != null -> "${elementMapper.name}($receiver)"
            sourceElementType.fullNameWithGenerics() == targetElementType.fullNameWithGenerics() -> receiver
            else -> recursiveGenerateMapperCall(
                sourceVar = receiver,
                sourceModel = sourceElementType,
                targetModel = targetElementType,
                visitedTypes = visitedTypes.toMutableSet(),
                currentMapperMethod = currentMapperMethod,
                isJava = isJava
            )
        }

        val forEachBlock = if (addContent.contains('\n')) {
            buildString {
                val forEachBlock = buildString {
                    appendLine("$sourceVar.forEach { $receiver ->")

                    val addBlock = buildString {
                        appendLine("add(")

                        appendLine(addContent.addIndent())

                        append(")")
                    }
                    appendLine(addBlock.addIndent())

                    append("}")
                }.addIndent()

                appendLine(forEachBlock)
            }
        } else {
            "$sourceVar.forEach { $receiver -> add($addContent) }".addIndent()
        }

        return buildString {
            appendLine("$collectionInitializer.apply {")
            appendLine(forEachBlock)
            append("}")
        }
    }

    private fun getCollectionInitializer(fullType: String): String {
        val elementType = extractElementTypeFromCollection(fullType)
        val baseType = fullType.substringBefore("<")

        return when {
            baseType.contains("MutableList", ignoreCase = true) -> "mutableListOf<$elementType>()"
            baseType.contains("ArrayList", ignoreCase = true) -> "arrayListOf<$elementType>()"
            baseType.contains("List", ignoreCase = true) -> "mutableListOf<$elementType>()"
            baseType.contains("MutableSet", ignoreCase = true) -> "mutableSetOf<$elementType>()"
            baseType.contains("HashSet", ignoreCase = true) -> "hashSetOf<$elementType>()"
            baseType.contains("LinkedHashSet", ignoreCase = true) -> "linkedSetOf<$elementType>()"
            baseType.contains("Set", ignoreCase = true) -> "mutableSetOf<$elementType>()"
            else -> "mutableListOf<$elementType>()"
        }
    }

    private fun extractElementTypeFromCollection(fullType: String): String {
        val regex = "<([^>]+)>".toRegex()
        return regex.find(fullType)?.groupValues?.get(1) ?: "Any"
    }

    private fun findDirectMapper(
        sourceModel: ClassModel,
        targetModel: ClassModel,
        currentMapperMethod: ImplMapperMethod
    ): MapperMethod? = mappers.firstOrNull { mapper ->
        mapper.sourceParameter.classModel.fullNameWithGenerics() == sourceModel.fullNameWithGenerics() && mapper.targetClass.fullNameWithGenerics() == targetModel.fullNameWithGenerics() && mapper.name != currentMapperMethod.name
    }

    private fun findMatchingSourceParameter(
        targetParam: Parameter, sourceModel: ClassModel
    ): Parameter? = sourceModel.parameters.find { it.name == targetParam.name }

    private fun String.addIndent(): String = this.prependIndent("    ")
}

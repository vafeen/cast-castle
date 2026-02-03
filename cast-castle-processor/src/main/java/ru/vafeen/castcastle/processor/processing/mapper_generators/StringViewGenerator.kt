package ru.vafeen.castcastle.processor.processing.mapper_generators

import com.google.devtools.ksp.symbol.KSNode
import ru.vafeen.castcastle.processor.libName
import ru.vafeen.castcastle.processor.logger
import ru.vafeen.castcastle.processor.processing.ProcessingVisibility
import ru.vafeen.castcastle.processor.processing.models.ClassModel
import ru.vafeen.castcastle.processor.processing.models.FuncParameter
import ru.vafeen.castcastle.processor.processing.models.ImplMapperClass
import ru.vafeen.castcastle.processor.processing.models.ImplMapperStandaloneFunction
import ru.vafeen.castcastle.processor.processing.models.MapperMethod
import ru.vafeen.castcastle.processor.processing.models.Parameter
import ru.vafeen.castcastle.processor.processing.utils.copyright
import ru.vafeen.castcastle.processor.processing.utils.getCollectionElementType
import ru.vafeen.castcastle.processor.processing.utils.isCollectionType
import java.time.LocalDateTime

internal class StringViewGenerator(private val mappers: List<MapperMethod>) {
    fun generateFuncsForMapperClass(
        baseClassType: String,
        implMapperClass: ImplMapperClass
    ): String {
        require(!isClassGenerationCalled) { "${StringViewGenerator::class.simpleName} must be called once for every implementation" }
        isClassGenerationCalled = true

        return buildString {
            appendLine("package ${implMapperClass.packageName}\n")
            appendLine("//updated: ${LocalDateTime.now()}\n")
            appendLine(copyright())

            appendLine(
                implMapperClass.implMethods.joinToString(separator = "\n") { method ->
                    generateExtensionForClass(
                        currentNode = method.baseMethod,
                        receiverName = baseClassType,
                        visibility = implMapperClass.visibility,
                        from = method.from,
                        to = method.to,
                        name = method.name,
                        isJava = implMapperClass.isJava
                    )
                }
            )
        }
    }


    fun generateStandaloneFunctions(
        packageName: String,
        implMapperStandaloneFunctions: List<ImplMapperStandaloneFunction>
    ): String =
        buildString {
            appendLine("package ${packageName}\n")
            appendLine("//updated: ${LocalDateTime.now()}\n")
            appendLine(copyright())
            implMapperStandaloneFunctions.forEach { implMapperStandaloneFunction ->
                appendLine(
                    generateMethodOnly(
                        currentNode = implMapperStandaloneFunction.declaration,
                        receiver = implMapperStandaloneFunction.from,
                        visibility = implMapperStandaloneFunction.visibility,
                        to = implMapperStandaloneFunction.to,
                        name = implMapperStandaloneFunction.name,
                        isExtension = implMapperStandaloneFunction.isExtension,
                        isJava = false
                    )
                )
            }
        }

    private fun generateExtensionForClass(
        currentNode: KSNode?,
        receiverName: String,
        visibility: ProcessingVisibility,
        from: Parameter,
        to: ClassModel,
        name: String,
        isJava: Boolean,
    ): String = buildString {
        val returnTypeName = to.fullNameWithGenerics()

        // Собираем все необходимые параметры
        val missingParameters = mutableListOf<Pair<String, String>>()

        // Сначала анализируем, какие параметры нужны
        recursiveGenerateMapperCall(
            currentNode = currentNode,
            sourceVar = from.name,
            sourceModel = from.classModel,
            targetModel = to,
            missingParameters = emptyMap(),
            currentMapperMethodName = name,
            isJava = isJava,
            onMissingParameter = { name, type ->
                missingParameters.add(name to type)
                null
            }
        )


        // Генерируем тело маппера с учетом всех параметров
        val mappingBody = recursiveGenerateMapperCall(
            currentNode = currentNode,
            sourceVar = from.name,
            sourceModel = from.classModel,
            targetModel = to,
            missingParameters = missingParameters.associate { it.first to it.first },
            currentMapperMethodName = name,
            isJava = isJava,
            onMissingParameter = { name, _ ->
                // В extension функции все параметры должны быть доступны
                name
            })
//        if (isExtension) {
        // Строим список параметров
        val fromAsParameter = (from.name to from.classModel.fullNameWithGenerics())
        val paramList =
            buildList {
                add(fromAsParameter)
                addAll(missingParameters)
            }.joinToString(", ") { it.asParameterDeclaration() }

        appendLine("${visibility.nameForFile()} fun ${receiverName}.${name}${libName}($paramList): $returnTypeName {")
//        } else {
//            val paramList = buildList {
//                add((from.name to from.classModel.fullNameWithGenerics()).asParameterDeclaration())
//                addAll(missingParameters.map { it.asParameterDeclaration() })
//            }.joinToString(", ")
//
//
//            appendLine("${visibility.nameForFile()} fun ${name}${libName}($paramList): $returnTypeName {")
//        }

        appendLine("return $mappingBody".addIndent())
        appendLine("}")
    }

    private fun generateMethodOnly(
        currentNode: KSNode?,
        receiver: FuncParameter,
        visibility: ProcessingVisibility,
        to: ClassModel,
        name: String,
        isExtension: Boolean,
        isJava: Boolean
    ): String =
        buildString {
            val returnTypeName = to.fullNameWithGenerics()
            val from = receiver


            // Собираем все необходимые параметры
            val missingParameters = mutableListOf<Pair<String, String>>()

            // Сначала анализируем, какие параметры нужны
            recursiveGenerateMapperCall(
                currentNode = currentNode,
                sourceVar = from.name,
                sourceModel = from.classModel,
                targetModel = to,
                missingParameters = emptyMap(),
                currentMapperMethodName = name,
                isJava = isJava,
                onMissingParameter = { name, type ->
                    missingParameters.add(name to type)
                    null
                }
            )


            // Генерируем тело маппера с учетом всех параметров
            val mappingBody = recursiveGenerateMapperCall(
                currentNode = currentNode,
                sourceVar = from.name,
                sourceModel = from.classModel,
                targetModel = to,
                missingParameters = missingParameters.associate { it.first to it.first },
                currentMapperMethodName = name,
                isJava = isJava,
                onMissingParameter = { name, _ ->
                    // В extension функции все параметры должны быть доступны
                    name
                })
            if (isExtension) {
                // Строим список параметров
                val paramList =
                    missingParameters.joinToString(", ") { "${it.first}: ${it.second}" }

                appendLine("${visibility.nameForFile()} fun ${receiver.classModel.fullNameWithGenerics()}.${name}${libName}($paramList): $returnTypeName {")
            } else {
                val paramList = buildList {
                    add((from.name to from.classModel.fullNameWithGenerics()).asParameterDeclaration())
                    addAll(missingParameters.map { it.asParameterDeclaration() })
                }.joinToString(", ")


                appendLine("${visibility.nameForFile()} fun ${name}${libName}($paramList): $returnTypeName {")
            }

            appendLine("return $mappingBody".addIndent())
            appendLine("}")
        }

    private var isClassGenerationCalled = false
    private var counter = 0
    private fun getReceiver(): String = "it${counter++}"
    private fun recursiveGenerateMapperCall(
        currentNode: KSNode?,
        sourceVar: String,
        sourceModel: ClassModel,
        targetModel: ClassModel,
        missingParameters: Map<String, String>,
        currentMapperMethodName: String,
        isJava: Boolean,
        onMissingParameter: (String, String) -> String?,
        visitedTypes: MutableSet<String> = mutableSetOf()
    ): String {
        val typeKey =
            "${sourceModel.fullNameWithGenerics()}->${targetModel.fullNameWithGenerics()}"
        if (typeKey in visitedTypes) {
            return "TODO(\"Circular mapping detected: $typeKey\")"
        }
        visitedTypes.add(typeKey)

        if (sourceModel.isCollectionType() && targetModel.isCollectionType()) {
            return generateCollectionMapping(
                currentNode = currentNode,
                sourceVar = sourceVar,
                sourceElementType = sourceModel.getCollectionElementType(),
                targetElementType = targetModel.getCollectionElementType(),
                targetCollectionFullType = targetModel.fullNameWithGenerics(),
                missingParameters = missingParameters,
                currentMapperMethodName = currentMapperMethodName,
                isJava = isJava,
                onMissingParameter = onMissingParameter,
                visitedTypes = visitedTypes
            )
        }

        val directMapper = findDirectMapper(sourceModel, targetModel, currentMapperMethodName)

        return if (directMapper != null) {
            "${directMapper.name}($sourceVar)"
        } else {
            generateConstructorCall(
                currentNode = currentNode,
                sourceVar = sourceVar,
                sourceModel = sourceModel,
                targetModel = targetModel,
                missingParameters = missingParameters,
                currentMapperMethodName = currentMapperMethodName,
                isJava = isJava,
                onMissingParameter = onMissingParameter,
                visitedTypes = visitedTypes
            )
        }
    }

    private fun generateConstructorCall(
        currentNode: KSNode?,
        sourceVar: String,
        sourceModel: ClassModel,
        targetModel: ClassModel,
        visitedTypes: MutableSet<String>,
        currentMapperMethodName: String,
        isJava: Boolean,
        missingParameters: Map<String, String>,
        onMissingParameter: (String, String) -> String?,
    ): String = buildString {
        val targetTypeName = targetModel.fullNameWithGenerics()

        appendLine("$targetTypeName(")
        val params = targetModel.parameters.map { targetParam ->
            val sourceParam = findMatchingSourceParameter(targetParam, sourceModel)

            val paramCall = sourceParam?.let { param ->
                val sourceFieldAccess = "$sourceVar.${param.name}"
                generateParameterMapping(
                    currentNode = currentNode,
                    sourceFieldAccess = sourceFieldAccess,
                    sourceParam = param,
                    targetParam = targetParam,
                    missingParameters = missingParameters,
                    currentMapperMethodName = currentMapperMethodName,
                    isJava = isJava,
                    onMissingParameter = onMissingParameter,
                    visitedTypes = visitedTypes.toMutableSet()
                )
            } ?: run {
                // Проверяем, есть ли параметр в missingParameters
                missingParameters[targetParam.name] ?: run {
                    // Параметр отсутствует - вызываем callback
                    onMissingParameter(
                        targetParam.name, targetParam.classModel.fullNameWithGenerics()
                    ) ?: "TODO(\"Provide value for ${targetParam.name}\")"
                }
            }

            "${if (!isJava) "${targetParam.name} = " else ""}$paramCall".addIndent()
        }
        appendLine(params.joinToString(separator = ",\n"))
        append(")")
    }

    private fun generateParameterMapping(
        currentNode: KSNode?,
        sourceFieldAccess: String,
        sourceParam: Parameter,
        targetParam: Parameter,
        missingParameters: Map<String, String>,
        currentMapperMethodName: String,
        isJava: Boolean,
        onMissingParameter: (String, String) -> String?,
        visitedTypes: MutableSet<String>
    ): String {
        return when {
            sourceParam.classModel.isCollectionType() && targetParam.classModel.isCollectionType() -> {
                generateCollectionMapping(
                    currentNode = currentNode,
                    sourceVar = sourceFieldAccess,
                    sourceElementType = sourceParam.classModel.getCollectionElementType(),
                    targetElementType = targetParam.classModel.getCollectionElementType(),
                    targetCollectionFullType = targetParam.classModel.fullNameWithGenerics(),
                    missingParameters = missingParameters,
                    currentMapperMethodName = currentMapperMethodName,
                    isJava = isJava,
                    onMissingParameter = onMissingParameter,
                    visitedTypes = visitedTypes
                )
            }

            sourceParam.classModel.fullNameWithGenerics() == targetParam.classModel.fullNameWithGenerics() -> {
                sourceFieldAccess
            }

            else -> {
                recursiveGenerateMapperCall(
                    currentNode = currentNode,
                    sourceVar = sourceFieldAccess,
                    sourceModel = sourceParam.classModel,
                    targetModel = targetParam.classModel,
                    visitedTypes = visitedTypes.toMutableSet(),
                    currentMapperMethodName = currentMapperMethodName,
                    isJava = isJava,
                    missingParameters = missingParameters,
                    onMissingParameter = onMissingParameter,
                )
            }
        }
    }

    private fun generateCollectionMapping(
        currentNode: KSNode?,
        sourceVar: String,
        sourceElementType: ClassModel?,
        targetElementType: ClassModel?,
        targetCollectionFullType: String,
        visitedTypes: MutableSet<String>,
        currentMapperMethodName: String,
        isJava: Boolean,
        missingParameters: Map<String, String>,
        onMissingParameter: (String, String) -> String?,
    ): String {
        if (sourceElementType == null || targetElementType == null) {
            logger?.warn(
                "Cannot determine collection element types for mapping",
                currentNode
            )
            return "$sourceVar // TODO: Add explicit mapper for collection types"
        }

        val elementMapper =
            findDirectMapper(sourceElementType, targetElementType, currentMapperMethodName)
        val receiver = getReceiver()
        val collectionInitializer = getCollectionInitializer(targetCollectionFullType)

        val addContent = when {
            elementMapper != null -> "${elementMapper.name}($receiver)"
            sourceElementType.fullNameWithGenerics() == targetElementType.fullNameWithGenerics() -> receiver
            else -> recursiveGenerateMapperCall(
                currentNode = currentNode,
                sourceVar = receiver,
                sourceModel = sourceElementType,
                targetModel = targetElementType,
                visitedTypes = visitedTypes.toMutableSet(),
                currentMapperMethodName = currentMapperMethodName,
                isJava = isJava,
                missingParameters = missingParameters,
                onMissingParameter = onMissingParameter,
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
            baseType.contains("MutableMap", ignoreCase = true) -> "mutableMapOf<$elementType>()"
            baseType.contains("HashMap", ignoreCase = true) -> "hashMapOf<$elementType>()"
            baseType.contains("LinkedHashMap", ignoreCase = true) -> "linkedMapOf<$elementType>()"
            baseType.contains("Map", ignoreCase = true) -> "mutableMapOf<$elementType>()"
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
        currentMapperMethodName: String? = null
    ): MapperMethod? = mappers.firstOrNull { mapper ->
        mapper.sourceParameter.classModel.fullNameWithGenerics() == sourceModel.fullNameWithGenerics() && mapper.targetClass.fullNameWithGenerics() == targetModel.fullNameWithGenerics() && mapper.name != currentMapperMethodName
    }

    private fun findMatchingSourceParameter(
        targetParam: Parameter, sourceModel: ClassModel
    ): Parameter? = sourceModel.parameters.find { it.name == targetParam.name }

    private fun String.addIndent(): String = this.prependIndent("    ")
    private fun Pair<Any, Any>.asParameterDeclaration(): String = "$first: $second"
}

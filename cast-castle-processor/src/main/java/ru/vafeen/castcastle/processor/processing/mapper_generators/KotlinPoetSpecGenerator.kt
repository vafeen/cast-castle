package ru.vafeen.castcastle.processor.processing.mapper_generators

import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.joinToCode
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
import ru.vafeen.castcastle.processor.processing.utils.fullNameWithGenerics
import ru.vafeen.castcastle.processor.processing.utils.getCollectionElementType
import ru.vafeen.castcastle.processor.processing.utils.isCollectionType
import ru.vafeen.castcastle.processor.processing.utils.toTypeName
import java.time.LocalDateTime

internal class KotlinPoetSpecGenerator(private val mappers: List<MapperMethod>) {
    fun generateFuncsForMapperClass(
        baseClassType: ClassName,
        implMapperClass: ImplMapperClass
    ): FileSpec {
        require(!isClassGenerationCalled) { "${KotlinPoetSpecGenerator::class.simpleName} must be called once for every implementation" }
        isClassGenerationCalled = true

        val fileBuilder = FileSpec.builder(
            packageName = implMapperClass.packageName,
            fileName = implMapperClass.name
        ).addHeader()

        implMapperClass.implMethods.forEachIndexed { index, method ->
            fileBuilder.addFunction(
                generateExtensionForClass(
                    currentNode = method.baseMethod,
                    receiverName = baseClassType,
                    visibility = implMapperClass.visibility,
                    from = method.from,
                    to = method.to,
                    name = method.name,
                    isJava = implMapperClass.isJava,
                    addCopyright = index == 0
                )
            )
        }

        return fileBuilder.build()
    }

    fun generateStandaloneFunctions(
        packageName: String,
        implMapperStandaloneFunctions: List<ImplMapperStandaloneFunction>
    ): FileSpec {
        val fileBuilder = FileSpec.builder(
            packageName = packageName,
            fileName = STANDALONE_FUNCTIONS_FILENAME
        ).addHeader()

        implMapperStandaloneFunctions.forEachIndexed { index, implMapperStandaloneFunction ->
            fileBuilder.addFunction(
                generateMethodOnly(
                    currentNode = implMapperStandaloneFunction.declaration,
                    receiver = implMapperStandaloneFunction.from,
                    visibility = implMapperStandaloneFunction.visibility,
                    to = implMapperStandaloneFunction.to,
                    name = implMapperStandaloneFunction.name,
                    isExtension = implMapperStandaloneFunction.isExtension,
                    isJava = false,
                    addCopyright = index == 0
                )
            )
        }

        return fileBuilder.build()
    }

    private fun FileSpec.Builder.addHeader(): FileSpec.Builder = apply {
        addFileComment("updated: %L", LocalDateTime.now())
    }

    private fun generateExtensionForClass(
        currentNode: KSNode?,
        receiverName: ClassName,
        visibility: ProcessingVisibility,
        from: Parameter,
        to: ClassModel,
        name: String,
        isJava: Boolean,
        addCopyright: Boolean,
    ): FunSpec {
        val missingParameters = collectMissingParameters(
            currentNode = currentNode,
            from = from,
            to = to,
            name = name,
            isJava = isJava
        )

        val mappingBody = recursiveGenerateMapperCall(
            currentNode = currentNode,
            sourceVar = from.name,
            sourceModel = from.classModel,
            targetModel = to,
            missingParameters = missingParameters,
            currentMapperMethodName = name,
            isJava = isJava,
            onMissingParameter = { parameterName, _ ->
                // In extension functions all parameters must be accessible
                parameterName
            })

        val funBuilder = FunSpec.builder("$name$libName")
            .receiver(receiverName)
            .returns(to.toTypeName())
            .addModifiers(visibility.toKModifier())
            .addParameter(from.name, from.classModel.toTypeName())

        if (addCopyright) {
            funBuilder.addKdoc(copyright())
        }

        missingParameters.forEach { (parameterName, parameterType) ->
            funBuilder.addParameter(parameterName, parameterType)
        }

        return funBuilder
            .addStatement("return %L", mappingBody)
            .build()
    }

    private fun generateMethodOnly(
        currentNode: KSNode?,
        receiver: FuncParameter,
        visibility: ProcessingVisibility,
        to: ClassModel,
        name: String,
        isExtension: Boolean,
        isJava: Boolean,
        addCopyright: Boolean,
    ): FunSpec {
        val from = receiver
        val missingParameters = collectMissingParameters(
            currentNode = currentNode,
            from = from,
            to = to,
            name = name,
            isJava = isJava
        )

        val mappingBody = recursiveGenerateMapperCall(
            currentNode = currentNode,
            sourceVar = from.name,
            sourceModel = from.classModel,
            targetModel = to,
            missingParameters = missingParameters,
            currentMapperMethodName = name,
            isJava = isJava,
            onMissingParameter = { parameterName, _ ->
                parameterName
            })

        val funBuilder = FunSpec.builder("$name$libName")
            .returns(to.toTypeName())
            .addModifiers(visibility.toKModifier())

        if (addCopyright) {
            funBuilder.addKdoc(copyright())
        }

        if (isExtension) {
            funBuilder.receiver(from.classModel.toTypeName())
        } else {
            funBuilder.addParameter(from.name, from.classModel.toTypeName())
        }

        missingParameters.forEach { (parameterName, parameterType) ->
            funBuilder.addParameter(parameterName, parameterType)
        }

        return funBuilder
            .addStatement("return %L", mappingBody)
            .build()
    }

    private fun collectMissingParameters(
        currentNode: KSNode?,
        from: FuncParameter,
        to: ClassModel,
        name: String,
        isJava: Boolean
    ): Map<String, TypeName> {
        val missingParameters = mutableListOf<Pair<String, TypeName>>()

        // First pass: analyze which parameters are needed
        recursiveGenerateMapperCall(
            currentNode = currentNode,
            sourceVar = from.name,
            sourceModel = from.classModel,
            targetModel = to,
            missingParameters = emptyMap(),
            currentMapperMethodName = name,
            isJava = isJava,
            onMissingParameter = { parameterName, parameterType ->
                missingParameters.add(parameterName to parameterType)
                null
            }
        )

        return missingParameters.associate { it.first to it.second }
    }

    private var isClassGenerationCalled = false
    private var counter = 0
    private fun getReceiver(): String = "it${counter++}"

    private fun recursiveGenerateMapperCall(
        currentNode: KSNode?,
        sourceVar: String,
        sourceModel: ClassModel,
        targetModel: ClassModel,
        missingParameters: Map<String, TypeName>,
        currentMapperMethodName: String,
        isJava: Boolean,
        onMissingParameter: (String, TypeName) -> String?,
        visitedTypes: MutableSet<String> = mutableSetOf()
    ): CodeBlock {
        val typeKey =
            "${sourceModel.fullNameWithGenerics()}->${targetModel.fullNameWithGenerics()}"
        if (typeKey in visitedTypes) {
            return CodeBlock.of("TODO(%S)", "Circular mapping detected: $typeKey")
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
            CodeBlock.of("%L(%L)", directMapper.name, sourceVar)
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
        missingParameters: Map<String, TypeName>,
        onMissingParameter: (String, TypeName) -> String?,
    ): CodeBlock {
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
                when {
                    targetParam.name in missingParameters -> CodeBlock.of("%L", targetParam.name)
                    else -> onMissingParameter(
                        targetParam.name, targetParam.classModel.toTypeName()
                    )?.let { CodeBlock.of("%L", it) }
                        ?: CodeBlock.of("TODO(%S)", "Provide value for ${targetParam.name}")
                }
            }

            if (isJava) paramCall else CodeBlock.of("%L = %L", targetParam.name, paramCall)
        }

        return CodeBlock.builder()
            .add("%T(\n", targetModel.toTypeName())
            .indent()
            .add(params.joinToCode(separator = ",\n"))
            .unindent()
            .add("\n)")
            .build()
    }

    private fun generateParameterMapping(
        currentNode: KSNode?,
        sourceFieldAccess: String,
        sourceParam: Parameter,
        targetParam: Parameter,
        missingParameters: Map<String, TypeName>,
        currentMapperMethodName: String,
        isJava: Boolean,
        onMissingParameter: (String, TypeName) -> String?,
        visitedTypes: MutableSet<String>
    ): CodeBlock = when {
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
            CodeBlock.of("%L", sourceFieldAccess)
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

    private fun generateCollectionMapping(
        currentNode: KSNode?,
        sourceVar: String,
        sourceElementType: ClassModel?,
        targetElementType: ClassModel?,
        targetCollectionFullType: String,
        visitedTypes: MutableSet<String>,
        currentMapperMethodName: String,
        isJava: Boolean,
        missingParameters: Map<String, TypeName>,
        onMissingParameter: (String, TypeName) -> String?,
    ): CodeBlock {
        if (sourceElementType == null || targetElementType == null) {
            logger?.warn(
                "Cannot determine collection element types for mapping",
                currentNode
            )
            return CodeBlock.of("%L // TODO: Add explicit mapper for collection types", sourceVar)
        }

        val elementMapper =
            findDirectMapper(sourceElementType, targetElementType, currentMapperMethodName)
        val receiver = getReceiver()
        val collectionInitializer = getCollectionInitializer(
            fullType = targetCollectionFullType,
            elementType = targetElementType.toTypeName()
        )

        val addContent: CodeBlock = when {
            elementMapper != null -> CodeBlock.of("%L(%L)", elementMapper.name, receiver)
            sourceElementType.fullNameWithGenerics() == targetElementType.fullNameWithGenerics() ->
                CodeBlock.of("%L", receiver)

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

        val addBlock = if (addContent.toString().contains('\n')) {
            CodeBlock.builder()
                .add("add(\n")
                .indent()
                .add(addContent)
                .unindent()
                .add("\n)")
                .build()
        } else {
            CodeBlock.of("add(%L)", addContent)
        }

        return CodeBlock.builder()
            .add("%L.apply {\n", collectionInitializer)
            .indent()
            .add("%L.forEach { %L ->\n", sourceVar, receiver)
            .indent()
            .add(addBlock)
            .unindent()
            .add("\n}")
            .unindent()
            .add("\n}")
            .build()
    }

    private fun getCollectionInitializer(fullType: String, elementType: TypeName): CodeBlock {
        val baseType = fullType.substringBefore("<")

        return when {
            baseType.contains("MutableList", ignoreCase = true) ->
                CodeBlock.of("mutableListOf<%T>()", elementType)

            baseType.contains("ArrayList", ignoreCase = true) ->
                CodeBlock.of("arrayListOf<%T>()", elementType)

            baseType.contains("List", ignoreCase = true) ->
                CodeBlock.of("mutableListOf<%T>()", elementType)

            baseType.contains("MutableSet", ignoreCase = true) ->
                CodeBlock.of("mutableSetOf<%T>()", elementType)

            baseType.contains("HashSet", ignoreCase = true) ->
                CodeBlock.of("hashSetOf<%T>()", elementType)

            baseType.contains("LinkedHashSet", ignoreCase = true) ->
                CodeBlock.of("linkedSetOf<%T>()", elementType)

            baseType.contains("Set", ignoreCase = true) ->
                CodeBlock.of("mutableSetOf<%T>()", elementType)

            baseType.contains("MutableMap", ignoreCase = true) ->
                CodeBlock.of("mutableMapOf<%L>()", extractElementTypeFromCollection(fullType))

            baseType.contains("HashMap", ignoreCase = true) ->
                CodeBlock.of("hashMapOf<%L>()", extractElementTypeFromCollection(fullType))

            baseType.contains("LinkedHashMap", ignoreCase = true) ->
                CodeBlock.of("linkedMapOf<%L>()", extractElementTypeFromCollection(fullType))

            baseType.contains("Map", ignoreCase = true) ->
                CodeBlock.of("mutableMapOf<%L>()", extractElementTypeFromCollection(fullType))

            else -> CodeBlock.of("mutableListOf<%T>()", elementType)
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

    companion object {
        private const val STANDALONE_FUNCTIONS_FILENAME = "StandaloneFunctions"
    }
}

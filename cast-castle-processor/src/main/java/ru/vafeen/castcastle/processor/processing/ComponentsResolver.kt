package ru.vafeen.castcastle.processor.processing

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Origin
import ru.vafeen.castcastle.annotations.CastCastleMapper
import ru.vafeen.castcastle.processor.logger
import ru.vafeen.castcastle.processor.processing.models.ClassModel
import ru.vafeen.castcastle.processor.processing.models.MapperClass
import ru.vafeen.castcastle.processor.processing.models.MapperMethod
import ru.vafeen.castcastle.processor.processing.models.MapperStandaloneFunction
import ru.vafeen.castcastle.processor.processing.models.Parameter


internal class ComponentsResolver(
    private val resolver: Resolver,
) {
    private val annotatedInterfaces = mutableListOf<MapperClass>()
    private val annotatedStandaloneFunctions = mutableListOf<MapperStandaloneFunction>()

    fun collectAnnotated() {
        getAllAnnotated().forEach {
            val parent = it.parent
            when (it) {
                is KSClassDeclaration -> {
                    annotatedInterfaces.add(it.toMapperClass())
                }

                is KSFunctionDeclaration if (parent == null || parent is KSFile)
//                    if it.parent == null
                    -> {
                    annotatedStandaloneFunctions.add(it.toMapperStandaloneFunction())
                }

                is KSFunctionDeclaration if (parent is KSClassDeclaration && !parent.isMappedAnnotated()) -> {
                    logger?.error(
                        "Parent of annotated func must be annotated with @${CastCastleMapper::class.simpleName} too",
                        it
                    )
                }
            }
        }
    }


    init {
        collectAnnotated()
    }

    fun getMapperInterfaces(): List<MapperClass> = annotatedInterfaces
    fun getMapperStandaloneFunctions(): List<MapperStandaloneFunction> =
        annotatedStandaloneFunctions

    fun getAllMappersForThisInterface(mapperClass: MapperClass): List<MapperMethod> {
        // todo сейчас это юзает только внутренние мапперы, а дальше будут еще и другие
        return mapperClass.mappers
    }

    private fun getAllAnnotated(): List<KSAnnotated> = resolver
        .getSymbolsWithAnnotation(CastCastleMapper::class.qualifiedName.toString())
        .toList()

    private fun KSClassDeclaration.toMapperClass(): MapperClass {

        val name = if (this.isCompanionObject) {
            val outerClass = this.parent as KSClassDeclaration
            "${outerClass.simpleName.asString()}.${this.simpleName.asString()}"
        } else {
            this.simpleName.asString()
        }

        return MapperClass(
            name = name,
            packageName = this.packageName.asString(),
            thisClass = this.containingFile,
            visibility = ProcessingVisibility.getDeclarationModifier(this),
            mappers = getAllMappers(),
            isJava = isJavaClass()
        )
    }

    private fun KSClassDeclaration.getAllMappers(): List<MapperMethod> {
        return this.getDeclaredFunctions()
            .toList()
            .filter { it.isValidMapper() }
            .map { it.toMapperMethod() }
    }

    private fun KSClassDeclaration.toClassModel(): ClassModel {
        return ClassModel(
            name = simpleName.asString(),
            packageName = packageName.asString(),
            thisClass = containingFile,
            visibility = ProcessingVisibility.getDeclarationModifier(this),
            parameters = getParameters(),
            typeArguments = listOf()
        )
    }

    private fun KSClassDeclaration.getParameters(): List<Parameter> {
        return when {
            this.isKotlinClass() -> getKotlinPrimaryConstructorParameters()
            this.isJavaClass() -> getJavaConstructorParameters()
            else -> emptyList()
        }
    }

    private fun KSClassDeclaration.isKotlinClass(): Boolean {
        return this.origin == Origin.KOTLIN || this.origin == Origin.KOTLIN_LIB
    }

    private fun KSClassDeclaration.isJavaClass(): Boolean {
        return this.origin == Origin.JAVA || this.origin == Origin.JAVA_LIB
    }

    private fun KSClassDeclaration.getJavaConstructorParameters(): List<Parameter> {
        val constructors = this.getAllConstructors()
            .filter { it.isPublic() }
            .sortedBy { it.parameters.size }

        val constructor = constructors.firstOrNull()
            ?: return emptyList()

        return constructor.parameters.map { it.toParameter() }
    }

    private fun KSClassDeclaration.getKotlinPrimaryConstructorParameters(): List<Parameter> =
        primaryConstructor?.parameters?.map { it.toParameter() }
            ?: emptyList()

    private fun KSClassDeclaration.getAllConstructors(): Sequence<KSFunctionDeclaration> {
        return this.declarations
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.isConstructor() }
    }

    private fun KSFunctionDeclaration.toMapperMethod(): MapperMethod {
        val returnType = this.returnType
            ?: throw IllegalStateException("Mapper method must have return type")

        return MapperMethod(
            sourceParameter = this.parameters.first().toParameter(),
            targetClass = returnType.toClassModel(),
            name = this.simpleName.asString(),
            isAbstract = this.isAbstract,
            isMapperAnnotated = this.isMappedAnnotated(),
            kspDeclaration = this
        )
    }

    private fun KSFunctionDeclaration.toMapperStandaloneFunction(): MapperStandaloneFunction {
        val returnType = this.returnType
        val receiver = this.extensionReceiver
        val parameter = this.parameters.firstOrNull()

        require(returnType != null) {
            "Standalone function annotated with ${CastCastleMapper::class.qualifiedName} must have only one parameter or receiver and return type".also {
                logger?.error(it)
            }
        }
        require(receiver != null || parameter != null) {
            "Standalone function annotated with ${CastCastleMapper::class.qualifiedName} must have only one parameter or receiver and return type".also {
                logger?.error(it)
            }
        }

        return MapperStandaloneFunction(
            packageName = this.packageName.asString(),
            name = this.simpleName.asString(),
            from = receiver?.toReceiverParameter() ?: parameter?.toParameter()
            ?: error("Receiver and parameter is null"),
            to = returnType.toClassModel(),
            declaration = this,
            isExtension = isExtension(),
            visibility = ProcessingVisibility.getDeclarationModifier(this)
        )
    }

    private fun KSFunctionDeclaration.isExtension(): Boolean =
        this.extensionReceiver != null

    private fun KSTypeReference.toReceiverParameter(): Parameter =
        Parameter(name = "this", classModel = this.toClassModel(), hasDefault = false)

    private fun KSFunctionDeclaration.isMappedAnnotated(): Boolean =
        this.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == CastCastleMapper::class.qualifiedName }

    private fun KSClassDeclaration.isMappedAnnotated(): Boolean =
        this.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == CastCastleMapper::class.qualifiedName }

    private fun KSValueParameter.toParameter(): Parameter = Parameter(
        name = this.name?.asString() ?: "unknown",
        classModel = this.type.toClassModel(),
        hasDefault = this.hasDefault,
//            isVararg = this.isVararg,
    )

    private fun KSTypeReference.typeArgs(): List<ClassModel> {
        val elementArgs = (this.element as? KSClassifierReference)?.typeArguments.orEmpty()
        if (elementArgs.any { it.type != null }) {
            return elementArgs.mapNotNull { it.type?.toClassModel() }
        }
        return resolve().arguments.mapNotNull { arg ->
            arg.type?.toClassModel()
        }
    }

    private fun KSTypeReference.toClassModel(): ClassModel {
        val classDeclaration = this.resolve().declaration as? KSClassDeclaration
            ?: throw IllegalArgumentException("KSType must represent a class declaration")

        return ClassModel(
            name = classDeclaration.simpleName.asString(),
            packageName = classDeclaration.packageName.asString(),
            thisClass = classDeclaration.containingFile,
            visibility = ProcessingVisibility.getDeclarationModifier(classDeclaration),
            parameters = classDeclaration.getParameters(),
            typeArguments = typeArgs()
        )
    }

    private fun KSFunctionDeclaration.isValidMapper(): Boolean {
        return this.parameters.size == 1 &&
                this.returnType != null
    }
}
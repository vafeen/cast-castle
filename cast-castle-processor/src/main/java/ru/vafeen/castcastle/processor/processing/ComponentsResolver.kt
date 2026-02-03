package ru.vafeen.castcastle.processor.processing

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Origin
import ru.vafeen.castcastle.annotations.CastCastleMapper
import ru.vafeen.castcastle.processor.processing.models.ClassModel
import ru.vafeen.castcastle.processor.processing.models.MapperClass
import ru.vafeen.castcastle.processor.processing.models.MapperMethod
import ru.vafeen.castcastle.processor.processing.models.Parameter


internal class ComponentsResolver(
    private val resolver: Resolver,
) {
    private val annotatedInterfaces = mutableListOf<MapperClass>()

    fun collectAnnotated() {
        getAllAnnotated().forEach {
            when {
                it is KSClassDeclaration
//                        && it.classKind == ClassKind.INTERFACE
                    -> {
                    annotatedInterfaces.add(it.toMapperClass())
                }
            }
        }
    }

    fun getMapperInterfaces(): List<MapperClass> = annotatedInterfaces
    fun getAllMappersForThisInterface(mapperClass: MapperClass): List<MapperMethod> {
        // todo сейчас это юзает только внутренние мапперы, а дальше будут еще и другие
        return mapperClass.mappers
    }

    private fun getAllAnnotated(): List<KSAnnotated> = resolver
        .getSymbolsWithAnnotation(CastCastleMapper::class.qualifiedName.toString())
        .toList()

    private fun KSClassDeclaration.toMapperClass(): MapperClass = MapperClass(
        name = this.simpleName.asString(),
        packageName = this.packageName.asString(),
        thisClass = this.containingFile,
        visibility = ProcessingVisibility.getClassAccessModifier(this),
        mappers = getAllMappers(),
        isJava = isJavaClass()
    )

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
            visibility = ProcessingVisibility.getClassAccessModifier(this),
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
        val returnType = this.returnType?.resolve()
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

    private fun KSFunctionDeclaration.isMappedAnnotated(): Boolean =
        this.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == CastCastleMapper::class.qualifiedName }

    private fun KSValueParameter.toParameter(): Parameter = Parameter(
        name = this.name?.asString() ?: "unknown",
        classModel = this.type.resolve().toClassModel(),
        hasDefault = this.hasDefault,
//            isVararg = this.isVararg,
    )

    private fun KSType.typeArgs() = this.arguments.mapNotNull { arg ->
        (arg.type?.resolve()?.declaration as? KSClassDeclaration)?.toClassModel()
    }

    private fun KSType.toClassModel(): ClassModel {
        val classDeclaration = this.declaration as? KSClassDeclaration
            ?: throw IllegalArgumentException("KSType must represent a class declaration")

        return ClassModel(
            name = classDeclaration.simpleName.asString(),
            packageName = classDeclaration.packageName.asString(),
            thisClass = classDeclaration.containingFile,
            visibility = ProcessingVisibility.getClassAccessModifier(classDeclaration),
            parameters = classDeclaration.getParameters(),
            typeArguments = typeArgs()
        )
    }

    private fun KSFunctionDeclaration.isValidMapper(): Boolean {
        return this.parameters.size == 1 &&
                this.returnType != null
    }
}
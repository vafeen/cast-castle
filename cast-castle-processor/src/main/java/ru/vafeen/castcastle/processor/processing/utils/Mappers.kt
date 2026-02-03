package ru.vafeen.castcastle.processor.processing.utils

import ru.vafeen.castcastle.processor.libName
import ru.vafeen.castcastle.processor.processing.models.ImplMapperClass
import ru.vafeen.castcastle.processor.processing.models.ImplMapperMethod
import ru.vafeen.castcastle.processor.processing.models.ImplMapperStandaloneFunction
import ru.vafeen.castcastle.processor.processing.models.MapperClass
import ru.vafeen.castcastle.processor.processing.models.MapperStandaloneFunction

internal fun MapperClass.toImplClassModel(): ImplMapperClass = ImplMapperClass(
    name = "${name}$libName",
    packageName = packageName,
    parent = thisClass,
    parentInterfaceName = name,
    visibility = visibility,
    implMethods = mappers
        .filter { it.isAbstract || it.isMapperAnnotated }
        .map {
            ImplMapperMethod(
                name = it.name,
                from = it.sourceParameter,
                to = it.targetClass,
                baseMethod = it.kspDeclaration
            )
        },
    isJava = isJava
)

internal fun MapperStandaloneFunction.toImplMapperStandaloneFunction(): ImplMapperStandaloneFunction =
    ImplMapperStandaloneFunction(
        packageName = packageName,
        name = name,
        from = from,
        to = to,
        declaration = declaration,
        isExtension = isExtension,
        visibility = visibility
    )
package ru.vafeen.castcastle.processor.processing

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.Modifier
import ru.vafeen.castcastle.annotations.CastCastleMapper
import ru.vafeen.castcastle.processor.logger

internal enum class ProcessingVisibility {
    PUBLIC {
        override fun nameForFile(): String = "public"
    },
    INTERNAL {
        override fun nameForFile(): String = "internal"
    };

    abstract fun nameForFile(): String

    companion object {


        fun getDeclarationModifier(
            ksDeclaration: KSDeclaration
        ): ProcessingVisibility {
            val error =
                { modifier: Modifier -> "Symbols annotated with ${CastCastleMapper::class.simpleName} cannot be ${modifier.name}" }
            return when {
                ksDeclaration.modifiers.contains(Modifier.PRIVATE) -> INTERNAL
                    .also {
                        logger?.error(
                            error(Modifier.PRIVATE),
                            ksDeclaration
                        )
                    }

                ksDeclaration.modifiers.contains(Modifier.PROTECTED) -> INTERNAL
                    .also {
                        logger?.error(
                            error(Modifier.PROTECTED),
                            ksDeclaration
                        )
                    }

                ksDeclaration.modifiers.contains(Modifier.INTERNAL) -> INTERNAL
//                ksClassDeclaration.modifiers.contains(Modifier.PUBLIC) -> PUBLIC
                else -> PUBLIC
            }
        }
    }
}

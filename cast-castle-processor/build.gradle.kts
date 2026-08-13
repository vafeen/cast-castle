plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.com.vanniktech.maven.publish)
}

group = project.findProperty("group").toString()
version = project.findProperty("version").toString()

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":cast-castle-annotations"))
    implementation(libs.symbol.processing.api)
    implementation(libs.kotlinpoet)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(
        project.findProperty("group").toString(),
        "cast-castle-processor",
        project.findProperty("version").toString()
    )

    pom {
        name.set("Cast Castle Processor")
        description.set("Cast Castle Processor")
        inceptionYear.set("2025")
        url.set("https://github.com/vafeen/Cast-Castle")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("vafeen")
                name.set("A")
                url.set("https://github.com/vafeen/")
            }
        }
        scm {
            url.set("https://github.com/vafeen/Cast-Castle")
            connection.set("scm:git:git://github.com/vafeen/Cast-Castle.git")
            developerConnection.set("scm:git:ssh://git@github.com/vafeen/Cast-Castle.git")
        }
    }
}
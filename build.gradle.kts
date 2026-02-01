plugins {
    `java-library`
    jacoco
    id("org.jetbrains.changelog") version "2.4.0"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "de.marhali"
version = "3.0.0"
description = "A lightweight library to parse and serialize JSON5 data"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(project.group.toString(), project.name, project.version.toString())

    pom {
        name.set(project.name)
        description.set(project.description)
        inceptionYear.set("2022")
        url.set("https://github.com/marhali/json5-java")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("marhali")
                name.set("Marcel Haßlinger")
                email.set("code@shield.marhali.de")
                url.set("https://marhali.de")
            }
        }
        scm {
            connection.set("scm:git:git@github.com:marhali/json5-java.git")
            developerConnection.set("scm:git:ssh://git@github.com/marhali/json5-java.git")
            url.set("https://github.com/marhali/json5-java/tree/main")
        }
    }
}

tasks {
    named<Test>("test") {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    named<JacocoReport>("jacocoTestReport") {
        dependsOn("test")
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

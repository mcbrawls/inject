plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("xyz.jpenilla.run-velocity") version "2.3.1" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16" apply false
    kotlin("jvm") version "2.1.0" apply false
    java
}

fun prop(name: String) = property(name) as String

version = prop("version")
group = prop("group")

subprojects {
    version = prop("version")
    group = prop("group")
}

allprojects {
    apply(plugin = "java")

    java {
        withSourcesJar()
    }
}

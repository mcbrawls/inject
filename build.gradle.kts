plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("io.papermc.paperweight.userdev") version "1.7.4" apply false
    kotlin("jvm") version "2.1.0" apply false
}

fun prop(name: String) = property(name) as String

version = prop("version")
group = prop("group")

subprojects {
    version = prop("version")
    group = prop("group")
}

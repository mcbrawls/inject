plugins {
    java
}

fun prop(name: String) = project.rootProject.property(name) as String

group = prop("group")
version = prop("version")

subprojects {
    apply(plugin = "java")

    group = prop("group")
    version = prop("version")

    repositories {
        mavenCentral()
    }
}

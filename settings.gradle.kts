pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":api")
include(":fabric")
include(":spigot")
include(":http")
include(":examples")
include(":examples:http-example")
include(":examples:javalin-example")
include(":examples:spring-example")
include(":jetty")
include(":javalin")
include(":spring")

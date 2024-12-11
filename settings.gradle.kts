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
include(":paper")
include(":spigot")
include(":http")
include(":examples")
include(":examples:fabric-example")
include(":examples:spigot-example")
include(":examples:paper-example")
include(":jetty")
include(":javalin")
include("spring")

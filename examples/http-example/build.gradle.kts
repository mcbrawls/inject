plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
}

fun prop(name: String) = project.rootProject.property(name) as String

version = prop("version")
group = prop("group")

dependencies {
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    mappings("net.fabricmc:yarn:${prop("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    implementation(project(":fabric", configuration = "namedElements"))
    implementation(project(":http"))
    implementation(project(":api"))
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

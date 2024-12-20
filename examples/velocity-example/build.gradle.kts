plugins {
    kotlin("jvm") version "2.1.0"
    id("xyz.jpenilla.run-velocity")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":velocity"))

    compileOnly("io.netty:netty-all:4.1.97.Final")
    compileOnly("org.slf4j:slf4j-api:1.7.30")

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks.runVelocity {
    version("3.4.0-SNAPSHOT")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

plugins {
    kotlin("jvm") version "2.1.0"
    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":spigot"))
    implementation(project(":http"))
    implementation(project(":api"))
    implementation(project(":jetty"))
    implementation(project(":ktor")) {
        isTransitive = false
    }

    val ktorVersion = "3.0.2"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty-jakarta:$ktorVersion")

    compileOnly("io.netty:netty-all:4.1.97.Final")
    compileOnly("org.slf4j:slf4j-api:1.7.30")

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks.runServer {
    minecraftVersion("1.21.4")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

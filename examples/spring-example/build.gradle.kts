plugins {
    java
    id("xyz.jpenilla.run-paper")
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":spigot"))
    implementation(project(":api"))
    implementation(project(":jetty"))

    implementation(project(":spring")) {
        isTransitive = false
    }

    // libraries provided via spigot libraries
    compileOnly("org.springframework.boot:spring-boot-starter-web:3.3.5")
    compileOnly("org.springframework.boot:spring-boot-starter-jetty:3.3.5")

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

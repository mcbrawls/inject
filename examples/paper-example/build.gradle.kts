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
    implementation(project(":paper"))
    implementation(project(":http"))
    implementation(project(":api"))
    implementation(project(":jetty"))

    implementation(project(":javalin")) {
        isTransitive = false
    }
    implementation("io.javalin:javalin:6.3.0")

    implementation(project(":spring")) {
        isTransitive = false
    }
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")
    implementation("org.springframework.boot:spring-boot-starter-jetty:3.3.5")

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

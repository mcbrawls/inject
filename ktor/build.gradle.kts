plugins {
    kotlin("jvm") version "2.1.0"
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "3.0.2"

    implementation(project(":api"))
    implementation(project(":jetty"))

    compileOnly("io.ktor:ktor-server-core:$ktorVersion")
    compileOnly("io.ktor:ktor-server-jetty-jakarta:$ktorVersion")
}

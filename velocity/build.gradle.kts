plugins {
    java
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":api"))
    compileOnly("io.netty:netty-all:4.1.97.Final")
    compileOnly("org.slf4j:slf4j-api:1.7.30")

    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])

            pom {
                name = "Inject (Velocity)"
                description = "A library for making injecting into Netty easier."
                url = "https://mcbrawls.net"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                        distribution = "repo"
                    }
                }
            }
        }
    }

    repositories {
        runCatching { // getenv throws if variable doesn't exist
            val mavenUser = System.getenv("MAVEN_USERNAME_ANDANTE")
            val mavenPass = System.getenv("MAVEN_PASSWORD_ANDANTE")

            maven {
                name = "Andante"
                url = uri("https://maven.andante.dev/releases/")

                credentials {
                    username = mavenUser
                    password = mavenPass
                }
            }
        }
    }
}

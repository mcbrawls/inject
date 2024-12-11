plugins {
    java
    `maven-publish`
}

fun prop(name: String) = project.rootProject.property(name) as String

group = prop("group")
version = prop("version")

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":api"))
    implementation(project(":jetty")) {
        isTransitive = false
    }

    compileOnly("org.springframework.boot:spring-boot-starter-web:3.3.5") {
        modules {
            exclude("org.springframework.boot", "spring-boot-starter-logging")
            module("org.springframework.boot:spring-boot-starter-tomcat") {
                replacedBy("org.springframework.boot:spring-boot-starter-jetty", "Use Jetty instead of Tomcat")
            }
        }
    }

    compileOnly("org.springframework.boot:spring-boot-starter-jetty:3.3.5")

    compileOnly("io.netty:netty-all:4.1.97.Final")
    compileOnly("org.slf4j:slf4j-api:1.7.30")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])

            pom {
                name = "Inject (Spring Boot)"
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

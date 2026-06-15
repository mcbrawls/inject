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
}

dependencies {
    implementation(project(":api"))
    implementation(project(":jetty")) {
        isTransitive = false
    }

    implementation("io.javalin:javalin:7.2.2")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])

            pom {
                name = "Inject (Javalin)"
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
            val mavenUser = providers.gradleProperty("MAVEN_USERNAME").get()
            val mavenPass = providers.gradleProperty("MAVEN_PASSWORD").get()

            maven {
                name = "Paulem"
                url = uri("https://maven.paulem.net/releases/")

                credentials {
                    username = mavenUser
                    password = mavenPass
                }
            }
        }
    }
}

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
    compileOnly("org.eclipse.jetty:jetty-server:12.0.14")
    compileOnly("io.netty:netty-all:4.1.97.Final")
    compileOnly("org.slf4j:slf4j-api:1.7.30")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])

            pom {
                name = "Inject (Jetty)"
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

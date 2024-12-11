plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1" apply false
    id("io.papermc.paperweight.userdev") version "1.7.4" apply false
}

version = project.property("version") as String
group = project.property("group") as String

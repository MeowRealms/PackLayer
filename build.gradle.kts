plugins {
    id("java")
}

val pluginVersion: String by project
val pluginDescription = "Optimize texture pack management on bungee/velocity to never double send the same pack."
group = "io.th0rgal.packsmanager"
version = pluginVersion

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://mvn.exceptionflug.de/repository/exceptionflug-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.elytrium.net/repo/")
}

dependencies {
    compileOnly("io.github.4drian3d:vpacketevents-api:1.1.0")
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-proxy:3.0.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    jar {
        filesMatching(arrayOf("bungee.yml", "velocity-plugin.json").asIterable()) {
            expand(mapOf("version" to pluginVersion, "description" to pluginDescription))
        }
    }

    compileJava.get().dependsOn(clean)
}

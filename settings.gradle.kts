plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val modName = extra.get("modName").toString().replace(" ", "-")
val mcVersion: String by extra
rootProject.name = "$modName-$mcVersion-NeoForge"

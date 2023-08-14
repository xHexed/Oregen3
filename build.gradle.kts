plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://www.uskyblock.ovh/maven/uskyblock/")
    maven("https://jitpack.io")
    maven("https://repo.songoda.com/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.extendedclip.com/content/repositories/public/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://repo.bg-software.com/repository/common/")
}

dependencies {
    implementation("com.github.cryptomorin:XSeries:9.5.0")
    implementation("org.bstats:bstats-bukkit-lite:1.7")
    implementation("com.bgsoftware.common.config:CommentedConfiguration:1.0.3")
    implementation("io.github.rysefoxx.inventory:RyseInventory-Plugin:1.5.6.2")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("com.wasteofplastic:askyblock:3.0.9.4")
    compileOnly("com.github.rlf:uSkyBlock-API:2.8.3")
    compileOnly("world.bentobox:bentobox:1.24.2-SNAPSHOT")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:1.11.1")
    compileOnly("com.github.Th0rgal:oraxen:1.160.0")
    compileOnly("com.songoda:skyblock:2.3.30")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.4.1-r4")
    compileOnly("com.wasteofplastic:acidisland:3.0.8.2")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

group = "me.banbeucmas"
version = "1.7.2"
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand("version" to project.version)
        }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    jar {
        dependsOn(shadowJar)
        enabled = false
    }
    shadowJar {
        minimize()
        relocate("org.bstats", "me.banbeucmas.oregen3.bstats")
        relocate("com.cryptomorin.xseries", "me.banbeucmas.oregen3.xseries")
        relocate("com.bgsoftware.common.config", "me.banbeucmas.oregen3.config")
        relocate("io.github.rysefoxx.inventory", "me.banbeucmas.oregen3.inventory")
        archiveFileName.set("Oregen3-${project.version}.jar")
    }
}

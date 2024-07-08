plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://www.uskyblock.ovh/maven/uskyblock/")
    maven("https://jitpack.io")
    maven("https://repo.songoda.com/repository/minecraft-plugins/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.extendedclip.com/content/repositories/public/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://repo.bg-software.com/repository/common/")
    maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
}

dependencies {
    implementation("com.github.cryptomorin:XSeries:10.0.0")
    implementation("org.bstats:bstats-bukkit-lite:1.7")
    implementation("com.bgsoftware.common.config:CommentedConfiguration:1.0.3")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        exclude("junit", "junit")
        exclude("org.yaml", "snakeyaml")
    }
    compileOnly("net.md-5:bungeecord-chat:1.20-R0.2") {
        isTransitive = false
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.wasteofplastic:askyblock:3.0.9.4")
    compileOnly("ovh.uskyblock:uSkyBlock-Core:3.0.0") {
        isTransitive = false
    }
    compileOnly("com.github.rlf:uSkyBlock-API:3.0.0")
    compileOnly("world.bentobox:bentobox:2.4.2-SNAPSHOT")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:1.11.1")
    compileOnly("com.github.Th0rgal:oraxen:1.172.0")
    compileOnly("com.craftaro:FabledSkyBlock:3.0.4")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")
    compileOnly("com.wasteofplastic:acidisland:3.0.8.2")
    compileOnly("com.iridium:IridiumSkyblock:4.0.9.1")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

group = "me.banbeucmas"
version = "1.8.0"
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
        archiveFileName.set("Oregen3-${project.version}.jar")
        exclude("META-INF/**")
    }
}

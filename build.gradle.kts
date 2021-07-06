plugins {
    java
    kotlin("jvm") version "1.5.20"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.lenlino"
version = "1.0-SNAPSHOT"

tasks.register("stage"){
    dependsOn("clean","shadowJar")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>{
    archiveFileName.set("bot.jar")
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("net.dv8tion:JDA:4.3.0_277")
    implementation("com.jagrosh:jda-utilities:3.0.5")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}



application {/*applicationブロックごと追記*/
    mainClass.set("MainKt")
}
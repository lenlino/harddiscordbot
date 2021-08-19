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
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("org.postgresql:postgresql:42.2.23")
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("net.dv8tion:JDA:4.3.0_277")
    implementation("com.jagrosh:jda-utilities:3.0.5")
    implementation("com.sedmelluq:lavaplayer:1.3.73")
    implementation("com.ibm.watson:ibm-watson:9.2.0")
    // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    // https://mvnrepository.com/artifact/net.arnx/jsonic
    implementation("net.arnx:jsonic:1.3.10")


}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}



application {/*applicationブロックごと追記*/
    mainClass.set("${group}.${rootProject.name}.MainKt")
}
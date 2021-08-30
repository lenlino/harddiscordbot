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
    maven("https://m2.chew.pro/releases")
    jcenter()
}

dependencies {
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("org.postgresql:postgresql:42.2.23.jre7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    implementation("net.dv8tion:JDA:4.3.0_277")
    implementation("pw.chew:jda-chewtils:1.21.0")
    implementation("com.sedmelluq:lavaplayer:1.3.73")
    implementation("com.ibm.watson:ibm-watson:9.2.1")
    // https://mvnrepository.com/artifact/org.java-websocket/Java-WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
    // https://mvnrepository.com/artifact/net.arnx/jsonic
    implementation("net.arnx:jsonic:1.3.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1-native-mt")
    implementation( "org.jetbrains.kotlin:kotlin-gradle-plugin")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}



application {/*applicationブロックごと追記*/
    mainClass.set("${group}.${rootProject.name}.MainKt")
}
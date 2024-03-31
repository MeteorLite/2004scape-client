plugins {
    kotlin("jvm") version "1.9.22"
    id("application")
}

group = "meteor"
version = "unspecified"

application {
    mainClass.set("meteor.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":rs2"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec> {
    workingDir = project.rootDir
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

kotlin {
    jvmToolchain(21)
}
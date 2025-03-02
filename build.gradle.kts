plugins {
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
}

group = "com.motycka.edu"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.h2database:h2")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.12") {
        exclude(group = "net.bytebuddy", module = "byte-buddy")
        exclude(group = "net.bytebuddy", module = "byte-buddy-agent")
    }
    testImplementation("net.bytebuddy:byte-buddy:1.15.7")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.15.7")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.3")
}

// Force Byte Buddy version to ensure compatibility with Java 23
configurations.all {
    resolutionStrategy {
        force("net.bytebuddy:byte-buddy:1.15.7")
        force("net.bytebuddy:byte-buddy-agent:1.15.7")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    println("Test task JVM args: ${jvmArgs?.joinToString(", ") ?: "none"}")
}
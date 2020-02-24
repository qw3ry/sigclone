import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.50"
  antlr
  id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "se.studieren.sigclone"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
  antlr("org.antlr:antlr4:4.+")

  implementation(kotlin("stdlib-jdk8"))

  implementation("org.reflections", "reflections", "0.9.+") /* class hierarchy */
  implementation("com.github.ajalt", "clikt", "2.+") /* cli parsing */
  implementation("org.apache.commons", "commons-collections4", "4.+") /* Bag<> */
  implementation("org.apache.commons", "commons-lang3", "3.+") /* StringUtils.splitByCharacterTypeCamelCase */
  implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.+") /* coroutines */
  implementation("org.apfloat", "apfloat", "1.9.+")

  implementation("org.nd4j", "nd4j-native-platform", "1.+") /* deeplearning4j backend */
  implementation("org.deeplearning4j", "deeplearning4j-core", "1.+") /* deeplearning4j */
  implementation("org.deeplearning4j", "deeplearning4j-nlp", "1.+") /* word2vec */

  runtimeOnly("org.slf4j", "slf4j-nop", "1.7+")

  testImplementation("io.kotlintest", "kotlintest-runner-junit5", "3.3.+")
}

sourceSets {
  val gen by creating { }

  getByName("main") {
    compileClasspath += gen.output
  }
}

configurations {
  val genImplementation by getting {
    extendsFrom(configurations["implementation"])
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.named<AntlrTask>("generateGrammarSource") {
  arguments = arguments + listOf("-visitor")
  outputDirectory = file("src/gen/java")
}

tasks.named<JavaCompile>("compileGenJava") {
  dependsOn("generateGrammarSource")
}

val test by tasks.getting(Test::class) {
  useJUnitPlatform { }
}

tasks.getByName<Jar>("jar") {
  manifest.attributes.apply {
    put("Main-Class", "main.MainKt")
    put("Class-Path", "libraries.jar")
  }
  from(sourceSets.getByName("gen").output)
}

open class DependencyJar : Jar()

tasks.register<Jar>("librariesJar") {
  exclude(sourceSets.flatMap { it.output.dirs.asIterable() }.map { it.absolutePath })
  archiveFileName.set("libraries.jar")
  from (
    configurations["runtimeClasspath"].asFileTree.files.map { zipTree(it) }
  )
  
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

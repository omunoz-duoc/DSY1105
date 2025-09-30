plugins {
	kotlin("jvm") version "2.2.10"
	application
}

group = "cl.duoc.dsy1105"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
	implementation("com.varabyte.kotter:kotter:1.1.2")
	testImplementation(kotlin("test"))
}

application {
	mainClass.set("cl.duoc.dsy1105.booksmart.MainKt")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(21)
}
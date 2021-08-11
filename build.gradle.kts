buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    `java-library`
    kotlin("jvm") version "1.4.32"
}

version = "0.0.1"

allprojects {
    group = "dev.burak.botlin"

    apply<JavaPlugin>()
    apply(plugin = "java-library")
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        jcenter()
        mavenLocal()
    }

    dependencies {
        compileOnly(files(System.getProperty("user.home") + "/.botlin/botlin-4.9.10-api.jar"))
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        build {
            doFirst {
                clean
            }
        }

        withType<Jar> {
            doLast {
                copy {
                    from("build/libs")
                    into(System.getProperty("user.home") + "/.botlin/scripts/")
                }
            }
        }

        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }
}

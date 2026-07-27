plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.6.2" apply false
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
}

compose.desktop {
    application {
        mainClass = "com.example.desktop.MainKt"
        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "BillCraftDesktop"
            packageVersion = "1.0.0"
            description = "BillCraft GST Billing & Invoicing Desktop Suite"
            vendor = "Acme Traders"
            windows {
                menuGroup = "BillCraft"
                shortcut = true
                dirChooser = true
            }
        }
    }
}

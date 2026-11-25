import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Plugins necesarios para Multiplatform, Android, Compose y el Compilador de Compose
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    // 1. CONFIGURACIÓN DEL TARGET ANDROID
    // Esto le dice a Kotlin que genere código compatible con la JVM de Android
    androidTarget {
        compilerOptions {
            // Establecemos Java 11 como objetivo (estándar actual para Android)
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // 2. CONFIGURACIÓN DE TARGETS iOS
    // Definimos soporte para iPhones reales (Arm64) y simuladores (SimulatorArm64)
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared" // Nombre de la librería que usará Xcode
            isStatic = true     // Recomendado para Compose Multiplatform
        }
    }

    // 3. FUENTES DE CÓDIGO Y DEPENDENCIAS (SourceSets)
    sourceSets {
        // commonMain: Aquí va el 95% del código (Clean Architecture, NestJS, UI)
        // Este código se comparte entre Android e iOS
        commonMain.dependencies {
            // Dependencias de UI (Compose Multiplatform)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            // Dependencias de ciclo de vida y ViewModels compartidos
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            //Libreria de iconos
            implementation(compose.materialIconsExtended)
            //Libreria para serializar los jsons
            implementation(libs.kotlinx.serialization.json)
            //Fechas multiplataforma (HeroCard de Home: ayer/hoy/mañana, saludo)
            implementation(libs.kotlinx.datetime)
            //Para camara y galeria
            implementation(libs.peekaboo.image.picker)
            //Para glassmorphism / liquid glass
            implementation(libs.haze)

            // ─── CAPA DATA/REMOTE (backend Spring Boot HABITIA-Back) ───
            // Ktor core: HttpClient, builders de peticiones, bearerAuth()
            implementation(libs.ktor.client.core)
            // Negotiation: serializa/deserializa DTOs automáticamente
            implementation(libs.ktor.client.content.negotiation)
            // JSON con kotlinx.serialization (plugin ya presente en el proyecto)
            implementation(libs.ktor.serialization.kotlinx.json)
            // Logging de tráfico HTTP (depuración de la integración)
            implementation(libs.ktor.client.logging)
            // DI (Koin) + Settings multiplataforma — patron del profesor
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.kotlinx.settings)
        }

        // androidMain: Código que solo corre en Android (ej. integraciones específicas)
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
            implementation(libs.androidx.activity.compose) // Para el launcher de cámara
            implementation(libs.compose.uiToolingPreview)
            // Storage persistente para la futura capa de Data (DataStoreUserPreferencesRepositoryImpl)
            implementation(libs.androidx.datastore.preferences)
            // Motor HTTP de Ktor para Android (OkHttp). El motor se elige POR
            // PLATAFORMA: commonMain define QUÉ peticiones hacer, cada target
            // define con QUÉ engine las ejecuta (igual que el booking-app).
            implementation(libs.ktor.client.android)
        }

        // iosMain: código compartido entre los 2 targets iOS (default hierarchy)
        iosMain.dependencies {
            // Motor HTTP de Ktor para iOS (NSUrlSession)
            implementation(libs.ktor.client.darwin)
        }

        // commonTest: Para pruebas unitarias compartidas
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// 4. CONFIGURACIÓN ESPECÍFICA DE ANDROID (Fuera del bloque Kotlin)
// Este bloque es necesario para que el plugin 'com.android.library' funcione
android {
    namespace = "pe.edu.upeu.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    // Sintaxis alternativa si la anterior sigue en rojo:
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
}

// 5. DEPENDENCIAS DE HERRAMIENTAS
dependencies {
    // Permite ver la vista previa de Compose en el IDE
    debugImplementation(libs.compose.uiTooling)
}
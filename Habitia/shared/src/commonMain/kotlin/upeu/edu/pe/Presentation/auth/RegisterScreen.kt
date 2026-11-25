package upeu.edu.pe.Presentation.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaButton
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.theme.habitiaAmbient

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel, //  POST /auth/register contra el backend
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // fecha de nacimiento — el backend la exige (@NotNull birthDate en
    // UserCreateDto). Formato ISO AAAA-MM-DD (valida AuthViewModel) porque es
    // el formato que espera LocalDate (Jackson) en el servidor
    var birthDate by remember { mutableStateOf("") }

    // Estado de autenticación + navegación por éxito (mismo patrón que Login).
    val authState by authViewModel.uiState.collectAsState()
    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            onRegisterSuccess() // sesión lista → Home con datos REALES del backend
            authViewModel.clearTransient()
        }
    }

    // GLASS REAL (misma arquitectura que Login — coherencia de marca)
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize()) {
        // ═══ CAPA FUENTE (hermana): el panel difumina este fondo ambiental
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // ═══ CAPA CONTENIDO: el panel glass como HERMANO → blur real
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
        // PANEL DE VIDRIO (entorno glass
        HabitiaGlassCard(
            hazeState = hazeState,
            // Forma -> cambia radio 26.dp (mas alto=mas redondo)
            shape = RoundedCornerShape(26.dp),
            // Vidrio -> Core/theme/Glass.kt glassTokens() (alpha/tinte)
            containerAlpha = 0.72f
        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                // Tamano -> cambia 80.dp aqui
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        // Color del icono -> Core/theme/Color.kt (onPrimaryContainer) o cambia tint= aqui
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        // Tamano -> cambia 40.dp aqui
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Empieza a diseñar tu rutina ideal",
                style = MaterialTheme.typography.bodyMedium,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre Completo") },
                leadingIcon = {
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                singleLine = true,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = {
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                singleLine = true,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Fecha de Nacimiento (exigido por el backend): placeholder
            // guía el formato ISO. Teclado de texto plano: no necesitamos un
            // keyboard numérico especial (queda como mejora el date picker).
            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Fecha de Nacimiento") },
                leadingIcon = {
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    Icon(Icons.Default.Cake, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                placeholder = { Text("AAAA-MM-DD") },
                singleLine = true,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // MENSAJE DE ERROR del backend (correo duplicado, contraseña corta...)
            authState.errorMessage?.let { mensaje ->
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            HabitiaButton(
                onClick = {
                    // Registro real: AuthViewModel valida en cliente y llama
                    // al repositorio (register + login automático).
                    authViewModel.register(name, email, password, birthDate)
                },
                enabled = !authState.isLoading, // evita doble submit
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (primary)
                containerColor = MaterialTheme.colorScheme.primary,
                // Color del texto -> Core/theme/Color.kt (onPrimary) o cambia color= aqui
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (authState.isLoading) {
                        CircularProgressIndicator(
                            // Tamano -> cambia 18.dp aqui
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            // Color del texto -> Core/theme/Color.kt (onPrimary) o cambia color= aqui
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (authState.isLoading) "Creando cuenta..." else "Registrarme",
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Inicia sesión",
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
        } // fin del panel de vidrio (HabitiaGlassCard)
        } // fin capa contenido
    } // fin raíz 2 capas (fuente + contenido)
}

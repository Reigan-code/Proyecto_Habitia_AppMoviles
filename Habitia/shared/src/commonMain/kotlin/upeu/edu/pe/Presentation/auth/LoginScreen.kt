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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaButton
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.theme.habitiaAmbient

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel, // MVVM real: valida contra el backend HABITIA-Back
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estado de autenticación (isLoading/errorMessage/isSuccess) observado
    // con collectAsState: la UI reacciona sin callbacks manuales.
    val authState by authViewModel.uiState.collectAsState()

    // NAVEGACIÓN POR ÉXITO: cuando AuthViewModel marca isSuccess (login real
    // aceptado por el backend), navegamos y limpiamos el estado para que, si
    // el usuario vuelve a Login, no re-dispare la navegación con datos viejos.
    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            onLoginSuccess()
            authViewModel.clearTransient() // resetea isSuccess/error/loading
        }
    }

    // GLASS REAL para el panel del login (auditoría #4): mismo sistema que el
    // resto de la app — fuente hermana + estado compartido con el panel.
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize()) {
        // ═══ CAPA FUENTE (hermana): el panel difumina este fondo ambiental
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        // ═══ CAPA CONTENIDO: el panel glass como HERMANO → el blur funciona
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
        // PANEL DE VIDRIO (entorno glass): agrupa el formulario de acceso en
        // una tarjeta translúcida con borde de luz y brillo especular — el
        // primer contacto del usuario con el lenguaje visual Liquid Glass de
        // la app. Alpha 0.72: se intuye el fondo sin sacrificar legibilidad.
        HabitiaGlassCard(
            hazeState = hazeState, // este es el blur que difumina los blobs ambientales a través del panel
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
            // Icono insignia en círculo tonal
            Surface(
                // Tamano -> cambia 80.dp aqui
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        // Color del icono -> Core/theme/Color.kt (onPrimaryContainer) o cambia tint= aqui
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        // Tamano -> cambia 42.dp aqui
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium.copy(
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Continúa organizando tu hogar y bienestar",
                style = MaterialTheme.typography.bodyMedium,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = {
                    // Color del icono -> Core/theme/Color.kt (primary) o cambia tint= aqui
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                // Forma -> cambia radio 14.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    // Color del texto -> Core/theme/Color.kt (outline) o cambia color= aqui
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // MENSAJE DE ERROR del backend (credenciales inválidas, backend
            // apagado...). Solo aparece si errorMessage no es null.
            authState.errorMessage?.let { mensaje ->
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Botón Ingresar — llama al AuthViewModel REAL (POST /auth/login).
            HabitiaButton(
                onClick = {
                    authViewModel.login(email, password) // managed en AuthViewModel
                },
                enabled = !authState.isLoading, // evita doble submit durante la red
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
                        // Indicador mientras viaja la petición al backend.
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
                        text = if (authState.isLoading) "Verificando..." else "Ingresar",
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (!authState.isLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            // Tamano -> cambia 18.dp aqui
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    style = MaterialTheme.typography.bodyMedium,
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Regístrate aquí",
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
        } // fin del panel de vidrio (HabitiaGlassCard)
        } // fin capa contenido
    } // fin raíz 2 capas (fuente + contenido)
}

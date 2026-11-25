package upeu.edu.pe.Presentation.UserMenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.GlassTextField
import upeu.edu.pe.Core.components.HabitiaButton
import upeu.edu.pe.Core.theme.habitiaAmbient
import upeu.edu.pe.rememberCameraLauncher

// ═══════════════════════════════════════════════════════════════════════════
// UserMenuScreen — PERFIL: SOLO DATOS DEL USUARIO (US-03 del plan Scrum).
//
// REDESIGN solicitado: antes mezclaba datos + todo el generador de paletas.
// Ahora:
//   • Aquí solo vive la INFORMACIÓN del usuario: foto, nombre, correo (solo
//     lectura — es el subject del JWT) y fecha de nacimiento. "Guardar
//     Cambios" hace PUT /Users/{id} REAL y replica en la sesión (el saludo
//     de Home se actualiza al momento).
//   • La PERSONALIZACIÓN (paletas, fondo, tipografía, modo oscuro) se movió
//     a AppearanceScreen — cada cosa en su apartado.
// Campos con GlassTextField (mismo método glass del sistema).
// ═══════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMenuScreen(
    viewModel: UserMenuViewModel,
    onBack: () -> Unit,
    // Logout REAL desde el perfil (App.kt hace session.clear() + navega).
    onLogout: () -> Unit = {},
    // NUEVO: navega a la pantalla de Personalización (apariencia).
    onOpenAppearance: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    // Patrón del fix Haze 1.2.x: fuente HERMANA detrás del contenido —
    // los GlassTextField/cards difuminan el ambiental real (blur activo).
    val hazeState = remember { HazeState() }
    val saving by viewModel.profileSaving.collectAsState()
    val message by viewModel.profileMessage.collectAsState()
    var showPhotoOptions by remember { mutableStateOf(false) }

    // Campos editables: precargados de la SESIÓN REAL (nombre/correo vienen
    // del backend en el login; birthDate del GET /Users/email).
    var editableName by remember(state.userName) { mutableStateOf(state.userName) }
    var editableBirthDate by remember(state.userName, state.userEmail) {
        mutableStateOf(state.birthDate ?: "")
    }

    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val galleryLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                viewModel.onProfileImagePicked(bytes)
            }
        }
    )

    val cameraLauncher = rememberCameraLauncher { bytes ->
        if (bytes != null) {
            viewModel.onProfileImagePicked(bytes)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ══ CAPA FUENTE (hermana) — blur real ══
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .habitiaAmbient()
        )
        androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                    // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = Color.Transparent // deja ver la capa fuente (hermana)
    ) { padding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── 1. FOTO DE PERFIL CON BADGE DE LÁPIZ ──
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(130.dp)
                    .clickable { showPhotoOptions = true }
            ) {
                if (state.profileImageBytes != null) {
                    Image(
                        bitmap = state.profileImageBytes!!.toImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            // Color de fondo -> Core/theme/Color.kt (surface) o cambia aqui
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                // Tamano -> cambia 68.dp aqui
                                modifier = Modifier.size(68.dp),
                                // Color del icono -> Core/theme/Color.kt (onPrimaryContainer) o cambia tint= aqui
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Surface(
                    shape = CircleShape,
                    // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                    color = MaterialTheme.colorScheme.primary,
                    // Tamano -> cambia 38.dp aqui
                    modifier = Modifier.size(38.dp),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Cambiar Foto",
                            // Color del icono -> Core/theme/Color.kt (onPrimary) o cambia tint= aqui
                            tint = MaterialTheme.colorScheme.onPrimary,
                            // Tamano -> cambia 18.dp aqui
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── 2. DATOS PERSONALES (campos GLASS con blur real) ──
            GlassTextField(
                value = editableName,
                onValueChange = { editableName = it },
                label = "Nombre completo",
                leadingIcon = Icons.Default.Person,
                hazeState = hazeState
            )

            // Correo SOLO LECTURA: el backend lo usa como identificador
            // (subject del JWT) y UserUpdateDto no permite cambiarlo.
            GlassTextField(
                value = state.userEmail.ifBlank { "Sin correo" },
                onValueChange = { },
                label = "Correo electrónico (no editable)",
                leadingIcon = Icons.Default.Email,
                readOnly = true,
                hazeState = hazeState
            )

            GlassTextField(
                value = editableBirthDate,
                onValueChange = { editableBirthDate = it },
                label = "Fecha de nacimiento (AAAA-MM-DD)",
                leadingIcon = Icons.Default.Cake,
                hazeState = hazeState
            )

            // Mensaje de resultado del guardado (éxito o error amigable).
            val saveMessage = message
            if (saveMessage != null) {
                Text(
                    text = saveMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (saveMessage.contains("✓")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            // ── 3. GUARDAR → PUT /Users/{id} REAL ──
            HabitiaButton(
                onClick = { viewModel.updateUserProfile(editableName, editableBirthDate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (primary)
                containerColor = MaterialTheme.colorScheme.primary,
                // Color del texto -> Core/theme/Color.kt (onPrimary) o cambia color= aqui
                contentColor = MaterialTheme.colorScheme.onPrimary,
                enabled = !saving
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        // Tamano -> cambia 20.dp aqui
                        modifier = Modifier.size(20.dp),
                        // Color del texto -> Core/theme/Color.kt (onPrimary) o cambia color= aqui
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Guardar Cambios",
                        // Peso de letra -> FontWeight.Bold (cambialo aqui)
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // ── 4. PERSONALIZACIÓN → apartado propio (AppearanceScreen) ──
            OutlinedButton(
                onClick = onOpenAppearance,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = null,
                    // Tamano -> cambia 20.dp aqui
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Personalizar Apariencia",
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // ── 5. CERRAR SESIÓN (con confirmación) ──
            var showLogoutConfirm by remember { mutableStateOf(false) }
            OutlinedButton(
                onClick = { showLogoutConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    // Tamano -> cambia 20.dp aqui
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cerrar Sesión",
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (showLogoutConfirm) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirm = false },
                    // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                    shape = RoundedCornerShape(20.dp),
                    // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                    // Color del contenedor -> Core/theme/Color.kt (surface)
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    title = { Text("¿Cerrar sesión?", fontWeight = FontWeight.Bold) },
                    text = { Text("Se borrará la sesión guardada en la app y volverás a la pantalla de acceso.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutConfirm = false
                                onLogout()
                            }
                        // Color del texto -> Core/theme/Color.kt (error) o cambia color= aqui
                        ) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutConfirm = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }

        // Modal para seleccionar Foto: Cámara o Galería
        if (showPhotoOptions) {
            AlertDialog(
                onDismissRequest = { showPhotoOptions = false },
                // Forma -> cambia radio 20.dp (mas alto=mas redondo)
                shape = RoundedCornerShape(20.dp),
                // Color del texto -> Core/theme/Color.kt (surface) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (surface)
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                title = { Text("Cambiar Foto de Perfil", fontWeight = FontWeight.Bold) },
                text = { Text("Elige si deseas tomar una foto o cargar una de tu galería:") },
                confirmButton = {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            cameraLauncher.launch()
                            showPhotoOptions = false
                        }
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tomar Foto")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            galleryLauncher.launch()
                            showPhotoOptions = false
                        }
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Elegir de Galería")
                    }
                }
            )
        }
    } // fin Scaffold
    } // fin Box raíz (2 capas: fuente hermana + contenido)
}

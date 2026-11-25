package upeu.edu.pe.Presentation.welcome

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import upeu.edu.pe.Core.components.HabitiaButton
import upeu.edu.pe.Core.theme.*

@Composable
fun WelcomeContent(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Color de fondo -> Core/theme/Color.kt (background) o cambia aqui
            .background(MaterialTheme.colorScheme.background)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icono insignia con contenedor tonal circular
            Surface (
                // Tamano -> cambia 100.dp aqui
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                // Color del texto -> Core/theme/Color.kt (primaryContainer) o cambia color= aqui
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = "Logo Habitia",
                        // Color del icono -> Core/theme/Color.kt (onPrimaryContainer) o cambia tint= aqui
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        // Tamano -> cambia 48.dp aqui
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Bienvenido a HABITIA",
                style = MaterialTheme.typography.headlineLarge.copy(
                    // Peso de letra -> FontWeight.Bold (cambialo aqui)
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tu espacio personal para organizar tareas, comidas, hábitos y bienestar cotidiano en sintonía.",
                style = MaterialTheme.typography.bodyLarge,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            // HabitiaButton adaptado a la firma actual con content lambda
            HabitiaButton(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                // Color del texto -> Core/theme/Color.kt (primary) o cambia color= aqui
                // Color del contenedor -> Core/theme/Color.kt (primary)
                containerColor = MaterialTheme.colorScheme.primary,
                // Color del texto -> Core/theme/Color.kt (onPrimary) o cambia color= aqui
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Comenzar",
                        // Peso de letra -> FontWeight.SemiBold (cambialo aqui)
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        // Tamano -> cambia 20.dp aqui
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

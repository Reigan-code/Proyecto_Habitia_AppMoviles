package upeu.edu.pe.Presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Pantalla Splash
 */
@Composable
fun SplashContent(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        // Velocidad del fade: cambia durationMillis=1200 (mas alto=mas lento)
        animationSpec = tween(durationMillis = 1200, easing = LinearOutSlowInEasing),
        label = "AlphaAnimation"
    )
    //
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        // Velocidad del zoom: cambia durationMillis=1200 o scale 0.85f
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "ScaleAnimation"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        // Tiempo que dura el Splash: cambia 1800 (ms)
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
        // Color de FONDO del splash -> Core/theme/Color.kt (background) o cambia aqui
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .scale(scaleAnim)
        ) {
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = "Logo Habitia",
            // Tamano del logo -> cambia 76.dp. Forma: Icons.Default.Spa
                modifier = Modifier.size(76.dp),
            // Color del logo -> Core/theme/Color.kt (primary) o cambia tint aqui
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "HABITIA",
                style = MaterialTheme.typography.headlineLarge.copy(
                // Tipografia del titulo -> cambia 32.sp / letterSpacing 2.sp / fontWeight
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                // Color del titulo -> Core/theme/Color.kt (onBackground) o cambia color aqui
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu hogar en sintonía",
                style = MaterialTheme.typography.bodyMedium.copy(
                    letterSpacing = 0.5.sp
                ),
                // Color del subtitulo -> Core/theme/Color.kt (onSurfaceVariant) o cambia aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

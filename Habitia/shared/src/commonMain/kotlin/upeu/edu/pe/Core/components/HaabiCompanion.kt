package upeu.edu.pe.Core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import upeu.edu.pe.Core.config.HaabiConfig
import upeu.edu.pe.Core.config.HaabiPlacement
import upeu.edu.pe.Data.remote.api.ApiClient

// ═══════════════════════════════════════════════════════════════════════════
// HaabiCompanion — mascota global + chat en Dialog centrado.
//
// • SIN matemáticas de offset: el Box raíz llena la pantalla y la mascota se
//   ubica con Alignment.BottomEnd + padding (valores en HaabiConfig,
//   Core/config/UiConfig.kt). Si la pantalla cambia de tamaño, sigue
//   anclada a la esquina.
// • La mascota es el BOTÓN del chat: al tocarla se abre un Dialog centrado
//   (no flota sobre el contenido) con botón X (Icons.Default.Close) para
//   cerrarlo.
// • Animación de entrada/salida suave: MutableTransitionState +
//   AnimatedVisibility (fadeIn/fadeOut + scaleIn/scaleOut). El patrón
//   MutableTransitionState mantiene el Dialog compuesto durante la salida,
//   algo que `if (show) Dialog { AnimatedVisibility }` no logra.
// • Si HaabiConfig.placement = INSIDE_RITUAL_CARD, aquí NO se dibuja la
//   mascota (la dibuja la tarjeta de rituales en HomeScreen); solo queda
//   activo el Dialog.
// ═══════════════════════════════════════════════════════════════════════════

private data class HaabiMsg(val fromUser: Boolean, val text: String, val route: String? = null)

@Composable
fun HaabiCompanion(
    visible: Boolean = true,
    onOpenHaabi: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    actions: upeu.edu.pe.Domain.actions.HaabiActions? = null
) {
    if (!visible) return

    // Estado de visibilidad ANIMABLE del chat: targetState = abierto/cerrado;
    // currentValue sigue la animación. El Dialog permanece en composición
    // mientras currentValue sea true (durante la salida).
    val chatVisibility = remember { MutableTransitionState(false) }
    val messages = remember {
        mutableStateListOf(HaabiMsg(false, "¡Hola! Soy Haabi 🐾. ¿Cómo te sientes hoy?"))
    }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // ── Arrastre de la mascota: offset persistente mientras la app está viva.
    //    Un flag distingue drag de tap para no abrir el chat al soltar tras
    //    arrastrar (el click se dispara igual; por eso se ignora si hasDragged).
    var mascotOffset by remember { mutableStateOf(IntOffset.Zero) }
    var hasDragged by remember { mutableStateOf(false) }

    // ── Overlay global: Box transparente a pantalla completa. La mascota se
    // alinea BottomEnd (sin offset manual). Si el placement es INSIDE_RITUAL_CARD,
    // la mascota la dibuja HomeScreen y aquí no se repite.
    Box(modifier = modifier.fillMaxSize()) {
        if (HaabiConfig.placement == HaabiPlacement.GLOBAL) {
            AnimatedMascot(
                visible = true,
                mascotSize = HaabiConfig.mascotSize,
                showHearts = HaabiConfig.showHearts,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset { mascotOffset }
                    .padding(end = HaabiConfig.marginEnd, bottom = HaabiConfig.bottomOffset)
                    // Arrastrar con el dedo mueve la mascota y recuerda la posición.
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { hasDragged = false },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (kotlin.math.abs(dragAmount.x) > 1f || kotlin.math.abs(dragAmount.y) > 1f) {
                                    hasDragged = true
                                }
                                mascotOffset = IntOffset(
                                    (mascotOffset.x + dragAmount.x).roundToInt(),
                                    (mascotOffset.y + dragAmount.y).roundToInt()
                                )
                            },
                            onDragEnd = { /* el offset queda donde se soltó */ }
                        )
                    }
                    // Tap (sin arrastre) abre el chat. Click tras drag se ignora.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!hasDragged) chatVisibility.targetState = true
                        hasDragged = false
                    }
            )
        }
    }

    // ── Chat: Dialog CENTRADO (comportamiento por defecto de Dialog), con X
    //    de cierre y animación de entrada/salida.
    if (chatVisibility.currentState || chatVisibility.targetState) {
        Dialog(onDismissRequest = { chatVisibility.targetState = false }) {
            AnimatedVisibility(
                visibleState = chatVisibility,
                enter = fadeIn(tween(220)) + scaleIn(
                    initialScale = 0.85f,
                    animationSpec = tween(220)
                ),
                exit = fadeOut(tween(180)) + scaleOut(
                    targetScale = 0.85f,
                    animationSpec = tween(180)
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(HaabiConfig.chatCorner),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth(HaabiConfig.chatDialogWidthFraction)
                        .heightIn(max = HaabiConfig.chatDialogMaxHeight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header: título + botón X de cierre
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Haabi 🐾",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = { chatVisibility.targetState = false }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar chat")
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .heightIn(max = HaabiConfig.chatListMaxHeight),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(messages) { m ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (m.fromUser) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = m.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (m.fromUser) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (!m.fromUser && m.route != null) {
                                        Spacer(Modifier.height(6.dp))
                                        TextButton(onClick = { onNavigate(m.route) }) {
                                            Text("Ir a ${m.route.replaceFirstChar { it.uppercase() }} →")
                                        }
                                    }
                                }
                            }
                            if (loading) {
                                item {
                                    Row(modifier = Modifier.padding(8.dp)) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BasicTextField(
                                value = input,
                                onValueChange = { input = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp),
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                ),
                                decorationBox = { inner ->
                                    if (input.isEmpty()) {
                                        Text(
                                            text = "Escríbele a Haabi...",
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                fontSize = 14.sp
                                            )
                                        )
                                    }
                                    inner()
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    val msg = input.trim()
                                    if (msg.isNotEmpty() && !loading) {
                                        messages.add(HaabiMsg(true, msg))
                                        input = ""
                                        loading = true
                                        scope.launch {
                                            try {
                                                val reply = try {
                                                    ApiClient.haabiChat(msg, actions = actions)
                                                } catch (e: Exception) {
                                                    println("[Habitia-Haabi] send FALLÓ: ${e.message}")
                                                    "Ups, no pude responder ahora 🐾"
                                                }
                                                val route = Regex("Ruta:\\s*/(\\w+)").find(reply)?.groupValues?.get(1)
                                                messages.add(HaabiMsg(false, reply, route))
                                            } finally {
                                                loading = false
                                            }
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar")
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        TextButton(
                            onClick = onOpenHaabi,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Abrir chat completo →", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

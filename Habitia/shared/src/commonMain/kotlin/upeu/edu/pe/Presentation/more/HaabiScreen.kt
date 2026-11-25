package upeu.edu.pe.Presentation.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import upeu.edu.pe.Core.components.AnimatedMascot
import upeu.edu.pe.Data.remote.api.ApiClient

// ═══════════════════════════════════════════════════════════════════════════
// HaabiScreen — apartado dedicado de Haabi (chat completo + mascota).
// Pantalla secundaria (stack navigator), accesible desde "Más" → "Mi Diario"? 
// No — desde "Más" → "Haabi". Evita que el chat flote sobre toda la app.
// ═══════════════════════════════════════════════════════════════════════════

private data class HaabiMsg(val fromUser: Boolean, val text: String, val route: String? = null)

private data class ChatSession(val id: String, val title: String, val messages: MutableList<HaabiMsg>)

@Composable
fun HaabiScreen(onBack: () -> Unit, onNavigate: (String) -> Unit = {}, actions: upeu.edu.pe.Domain.actions.HaabiActions? = null) {
    val sessions = remember { mutableStateListOf(ChatSession("1", "Chat 1", mutableStateListOf(HaabiMsg(false, "Hola! Soy Haabi, tu companero de habitos. En que puedo ayudarte hoy?")))) }
    var activeId by remember { mutableStateOf("1") }
    val messages = sessions.first { it.id == activeId }.messages
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var persona by remember { mutableStateOf("") }

    fun newChat() {
        val id = (sessions.size + 1).toString()
        sessions.add(ChatSession(id, "Chat $id", mutableStateListOf(HaabiMsg(false, "Hola! Soy Haabi, tu companero de habitos. En que puedo ayudarte hoy?"))))
        activeId = id
    }

    fun deleteChat(id: String) {
        if (sessions.size <= 1) {
            sessions[0].messages.clear()
            sessions[0].messages.add(HaabiMsg(false, "Hola! Soy Haabi, tu companero de habitos. En que puedo ayudarte hoy?"))
            activeId = sessions[0].id
            return
        }
        sessions.removeAll { it.id == id }
        if (activeId == id) activeId = sessions.last().id
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Spacer(Modifier.width(8.dp))
            AnimatedMascot(visible = true, mascotSize = 60.dp, showHearts = false)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Haabi",
                // Peso de letra -> FontWeight.Bold (cambialo aqui)
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                // Color del texto -> Core/theme/Color.kt (onBackground) o cambia color= aqui
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Chips de conversaciones (nueva / eliminar)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sessions.forEach { s ->
                Surface(
                    // Forma -> cambia radio 16.dp (mas alto=mas redondo)
                    shape = RoundedCornerShape(16.dp),
                    color = if (s.id == activeId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { activeId = s.id }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(s.title, style = MaterialTheme.typography.labelSmall, color = if (s.id == activeId) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        // Tamano -> cambia 14.dp aqui
                        Icon(Icons.Default.Close, contentDescription = "Eliminar chat", modifier = Modifier.size(14.dp).clickable { deleteChat(s.id) })
                    }
                }
            }
            IconButton(onClick = { newChat() }) { Icon(Icons.Default.Add, contentDescription = "Nueva conversacion") }
        }

        // Selector de trato (persona)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
            Text("Trato:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            listOf("", "casual y cercano", "formal y respetuoso", "motivador y energico").forEach { p ->
                FilterChip(
                    selected = persona == p,
                    onClick = { persona = p },
                    label = { Text(if (p.isBlank()) "Normal" else p, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Mascota grande + chat

        // Mascota grande + chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedMascot(visible = true, mascotSize = 120.dp, showHearts = true)
            Text(
                text = "Tu compañero de hábitos y bienestar. Cuéntame cómo te sientes o pídeme consejos.",
                style = MaterialTheme.typography.bodySmall,
                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Chat
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { m ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Forma -> cambia radio 12.dp (mas alto=mas redondo)
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
                            Text("Ir a ${m.route.replaceFirstChar { it.uppercase() }} ->")
                        }
                    }
                }
            }
            if (loading) {
                item {
                    CircularProgressIndicatorPlaceholder()
                }
            }
        }

        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 104.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier
                    .weight(1f)
                    // Forma -> cambia radio 12.dp (mas alto=mas redondo)
                    .clip(RoundedCornerShape(12.dp))
                    // Color de fondo -> Core/theme/Color.kt (surfaceVariant) o cambia aqui
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                textStyle = TextStyle(
                    // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    // Tipografia -> cambia fontSize = 14.sp o usa MaterialTheme.typography
                    fontSize = 14.sp
                ),
                decorationBox = { inner ->
                    if (input.isEmpty()) {
                        Text(
                            text = "Escríbele a Haabi...",
                            style = TextStyle(
                                // Color del texto -> Core/theme/Color.kt (onSurfaceVariant) o cambia color= aqui
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                // Tipografia -> cambia fontSize = 14.sp o usa MaterialTheme.typography
                                fontSize = 14.sp
                            )
                        )
                    }
                    inner()
                }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val msg = input.trim()
                    if (msg.isNotEmpty() && !loading) {
                        messages.add(HaabiMsg(true, msg))
                        input = ""
                        loading = true
                        scope.launch {
                            try {
                                val reply = try {
                                    val history = messages.dropLast(1).map { it.fromUser to it.text }
                                    ApiClient.haabiChat(msg, actions = actions, history = history, persona = persona)
                                } catch (e: Exception) {
                                    println("[Habitia-Haabi] send FALLO: ${e.message}")
                                    "Ups, no pude responder ahora. Intentemos de nuevo! (Haabi)"
                                }
                                val route = Regex("Ruta:\\s*/(\\w+)").find(reply)?.groupValues?.get(1)
                                messages.add(HaabiMsg(false, reply, route))
                            } finally {
                                loading = false
                            }
                        }
                    }
                },
                enabled = !loading
            ) {
                Text("Enviar")
            }
        }
    }
}

@Composable
private fun CircularProgressIndicatorPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        CircularProgressIndicator(
            // Tamano -> cambia 20.dp aqui
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp
        )
    }
}

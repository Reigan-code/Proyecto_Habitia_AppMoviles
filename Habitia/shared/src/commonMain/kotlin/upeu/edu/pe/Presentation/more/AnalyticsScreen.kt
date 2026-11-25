package upeu.edu.pe.Presentation.more

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import upeu.edu.pe.Core.components.HabitiaGlassCard
import upeu.edu.pe.Core.components.HabitiaSectionTitle
import upeu.edu.pe.Core.theme.habitiaAmbient

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onBack: () -> Unit
) {
    val podium by viewModel.podium.collectAsState()
    val weekly by viewModel.weekly.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val loading by viewModel.loading.collectAsState()

    LaunchedEffect(Unit) { viewModel.refresh() }

    val hazeState = androidx.compose.runtime.remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize().hazeSource(hazeState).habitiaAmbient()
        )
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 24.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") }
                        HabitiaSectionTitle(
                            title = "Analíticas y recompensas",
                            subtitle = "Tu progreso, podio de hábitos y logros desbloqueados"
                        )
                    }
                }

                item {
                    HabitiaGlassCard(hazeState = hazeState) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Resumen semanal", fontWeight = FontWeight.SemiBold)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Stat("Tareas", weekly.tasksDone)
                                Stat("Hábitos", weekly.habitsDone)
                                Stat("Comidas", weekly.mealsLogged)
                                Stat("Recordatorios", weekly.remindersDone)
                            }
                        }
                    }
                }

                item {
                    HabitiaGlassCard(hazeState = hazeState) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Podio de hábitos", fontWeight = FontWeight.SemiBold)
                            if (podium.isEmpty()) {
                                Text("Aún no hay hábitos completados.", style = MaterialTheme.typography.bodySmall)
                            } else {
                                podium.forEach { e ->
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Text(e.reward, modifier = Modifier.width(40.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(e.habitName, fontWeight = FontWeight.Medium)
                                            Text("${e.completionCount} completions", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text("#${e.position}", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    HabitiaGlassCard(hazeState = hazeState) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Balance", fontWeight = FontWeight.SemiBold)
                            Text("Productividad: ${balance.productivity.toInt()}%")
                            Text("Bienestar: ${balance.wellness.toInt()}%")
                            Text("Balance: ${balance.balance.toInt()}%", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    HabitiaGlassCard(hazeState = hazeState) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Recompensas", fontWeight = FontWeight.SemiBold)
                            rewards.forEach { r ->
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(r.icon, modifier = Modifier.width(32.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(r.title, fontWeight = FontWeight.Medium)
                                        Text(r.description, style = MaterialTheme.typography.bodySmall)
                                    }
                                    Icon(
                                        if (r.unlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = if (r.unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }

                if (loading) {
                    item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)) }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value.toString(), fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

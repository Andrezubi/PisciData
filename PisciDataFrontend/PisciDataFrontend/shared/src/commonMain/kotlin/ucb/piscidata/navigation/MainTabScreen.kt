package ucb.piscidata.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import ucb.piscidata.chat.presentation.screen.ChatScreen
import ucb.piscidata.database.presentation.screen.DatabaseScreen
import ucb.piscidata.profile.presentation.screen.ProfileScreen
import ucb.piscidata.reports.presentation.screen.ReportsScreen
import ucb.piscidata.tasks.presentation.screen.TasksScreen
import ucb.piscidata.ui.components.*

enum class MainTab {
    CHAT, DB, TAREAS, REPORTES, PERFIL
}

@Composable
fun MainTabScreen(
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf(MainTab.CHAT) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White
            ) {
                listOf(
                    MainTab.CHAT to "Asistente IA",
                    MainTab.DB to "Base de datos",
                    MainTab.TAREAS to "Tareas",
                    MainTab.REPORTES to "Reportes",
                    MainTab.PERFIL to "Perfil"
                ).forEach { (tab, label) ->
                    val selected = currentTab == tab
                    val tint = if (selected) Color(0xFF1577C8) else Color(0xFF91A5AD)
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            when (tab) {
                                MainTab.CHAT -> ChatIcon(color = tint)
                                MainTab.DB -> DatabaseIcon(color = tint)
                                MainTab.TAREAS -> TasksIcon(color = tint)
                                MainTab.REPORTES -> ReportsIcon(color = tint)
                                MainTab.PERFIL -> ProfileIcon(color = tint)
                            }
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip,
                                color = tint
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                MainTab.CHAT -> ChatScreen()
                MainTab.DB -> DatabaseScreen()
                MainTab.TAREAS -> TasksScreen()
                MainTab.REPORTES -> ReportsScreen()
                MainTab.PERFIL -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

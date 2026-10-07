package ucb.piscidata.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import ucb.piscidata.chat.presentation.screen.ChatScreen
import ucb.piscidata.database.presentation.screen.DatabaseScreen
import ucb.piscidata.profile.presentation.screen.ProfileScreen
import ucb.piscidata.reports.presentation.screen.ReportsScreen
import ucb.piscidata.tasks.presentation.screen.TasksScreen

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
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Text(
                                text = when (tab) {
                                    MainTab.CHAT -> "💬"
                                    MainTab.DB -> "🗄️"
                                    MainTab.TAREAS -> "📋"
                                    MainTab.REPORTES -> "📊"
                                    MainTab.PERFIL -> "👤"
                                },
                                fontSize = 18.sp
                            )
                        },
                        label = { Text(label, fontSize = 10.sp) }
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

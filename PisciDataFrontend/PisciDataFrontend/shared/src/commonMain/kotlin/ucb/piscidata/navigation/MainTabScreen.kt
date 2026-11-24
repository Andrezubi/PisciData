package ucb.piscidata.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Surface(color = Color.White) {
                Column(modifier = Modifier.fillMaxWidth().navigationBarsPadding()) {
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFEDF2F7))
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier.height(64.dp)
                    ) {
                        listOf(
                            MainTab.CHAT to "Chat",
                            MainTab.DB to "Granja",
                            MainTab.TAREAS to "Tareas",
                            MainTab.REPORTES to "Reportes",
                            MainTab.PERFIL to "Perfil"
                        ).forEach { (tab, label) ->
                            val selected = currentTab == tab
                            val tint = if (selected) Color(0xFF1577C8) else Color(0xFF94A3B8)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentTab = tab },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF1577C8),
                                    selectedTextColor = Color(0xFF1577C8),
                                    indicatorColor = Color(0xFFEFF6FF),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF94A3B8)
                                ),
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
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
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
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)).togetherWith(fadeOut(animationSpec = tween(140)))
                },
                label = "MainTabTransition"
            ) { tab ->
                when (tab) {
                    MainTab.CHAT -> ChatScreen()
                    MainTab.DB -> DatabaseScreen()
                    MainTab.TAREAS -> TasksScreen()
                    MainTab.REPORTES -> ReportsScreen()
                    MainTab.PERFIL -> ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }
}

package ucb.piscidata.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header profile
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color(0xFF1577C8)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("CM", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Carlos Mendoza", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                Text("Administrador · Piscigranja El Manantial", fontSize = 12.sp, color = Color(0xFF6B818B))
            }
        }

        // Options
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Cuenta", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                TextButton(onClick = {}) { Text("Cambiar contraseña", color = Color(0xFF16313D)) }
                TextButton(onClick = {}) { Text("Notificaciones", color = Color(0xFF16313D)) }
                TextButton(onClick = {}) { Text("Preferencias", color = Color(0xFF16313D)) }
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEEEE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cerrar sesión", color = Color(0xFFE05A5A), fontWeight = FontWeight.Bold)
        }
    }
}

package ucb.piscidata.reports.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.ui.components.ReportsIcon

@Composable
fun ReportsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Reportes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
            Text("Desempeño productivo general", fontSize = 12.sp, color = Color(0xFF6B818B))
        }

        // KPI Cards
        listOf(
            Triple("Ciclos productivos activos", "8 activos", "↑ 2 vs anterior"),
            Triple("Producción estimada", "42,6 t", "↑ 8.4% s/ proy"),
            Triple("Mortalidad promedio", "2,3%", "↓ 0.6% en obj")
        ).forEach { (label, value, trend) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(label, fontSize = 12.sp, color = Color(0xFF6B818B))
                        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                    }
                    Text(trend, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1577C8))
                }
            }
        }

        // AI Observation card
        Surface(
            color = Color(0xFFE8F4FC),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReportsIcon(Color(0xFF1577C8), modifier = Modifier.size(20.dp))
                Text(
                    text = "La mortalidad se mantiene por debajo del umbral objetivo. El FCR mejoró 0,18 puntos.",
                    fontSize = 12.sp,
                    color = Color(0xFF1577C8)
                )
            }
        }
    }
}

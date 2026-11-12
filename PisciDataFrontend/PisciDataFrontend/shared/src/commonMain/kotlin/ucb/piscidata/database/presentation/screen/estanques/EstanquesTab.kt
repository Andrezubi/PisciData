package ucb.piscidata.database.presentation.screen.estanques

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.presentation.screen.components.InfoRow
import ucb.piscidata.database.presentation.viewmodel.DatabaseState

@Composable
fun EstanquesTab(state: DatabaseState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.ponds) { pond ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pond.code, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                            Text(pond.nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                        }
                        Surface(
                            color = if (pond.cicloActivo != null) Color(0xFFE8F8F1) else Color(0xFFDCE8ED),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (pond.cicloActivo != null) "Activo" else "Sin ciclo",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (pond.cicloActivo != null) Color(0xFF19A974) else Color(0xFF6B818B)
                            )
                        }
                    }
                    InfoRow("Forma", pond.forma.name)
                    InfoRow("Área", "${pond.area} m²")
                    InfoRow("Profundidad", "${pond.profundidad} m")
                    if (pond.transparenciaCm != null) {
                        InfoRow("Transparencia", "${pond.transparenciaCm} cm")
                    }
                    InfoRow("pH", pond.ph.toString())
                    InfoRow("Temperatura", "${pond.temperatura}°C")
                    InfoRow("Oxígeno disuelto", "${pond.oxigeno} mg/L")
                }
            }
        }
    }
}

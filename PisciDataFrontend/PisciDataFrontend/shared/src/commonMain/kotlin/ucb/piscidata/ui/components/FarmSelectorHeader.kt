package ucb.piscidata.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.domain.model.FarmModel

/**
 * Barra superior unificada, minimalista y luminosa (estilo Linear / Notion / ChatGPT).
 * Extiende el fondo blanco detrás del status bar (eliminando franjas partidas) y permite
 * seleccionar la granja desde una cápsula compacta e integrar acciones contextuales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmSelectorHeader(
    farms: List<FarmModel>,
    selectedFarmId: Int,
    onSelectFarm: (Int) -> Unit,
    subtitle: String? = null,
    leadingAction: (@Composable () -> Unit)? = null,
    trailingActions: (@Composable RowScope.() -> Unit)? = null
) {
    val currentFarmName = farms.find { it.id == selectedFarmId }?.name ?: "Mi Piscigranja"
    var showFarmSheet by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (leadingAction != null) {
                        leadingAction()
                    } else {
                        PisciDataLogo(darkTheme = false, modifier = Modifier.size(30.dp))
                    }

                    // Cápsula compacta de selección de granja
                    Surface(
                        onClick = { if (farms.isNotEmpty()) showFarmSheet = true },
                        color = Color(0xFFF4F7FA),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5ECF2))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = currentFarmName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B2B3B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (!subtitle.isNullOrBlank()) {
                                    Text(
                                        text = subtitle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Text(
                                text = "▾",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (trailingActions != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        content = trailingActions
                    )
                }
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFEDF2F7))
        }
    }

    if (showFarmSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFarmSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Cambiar piscigranja",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0B2B3B)
                )
                Text(
                    text = "Selecciona la unidad productiva activa",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                farms.forEach { farm ->
                    val selected = farm.id == selectedFarmId
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectFarm(farm.id)
                                showFarmSheet = false
                            },
                        color = if (selected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            1.dp,
                            if (selected) Color(0xFF1577C8) else Color(0xFFE5ECF2)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = farm.name,
                                fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = Color(0xFF0B2B3B)
                            )
                            if (selected) {
                                Text(
                                    text = "✓ Activa",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1577C8)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

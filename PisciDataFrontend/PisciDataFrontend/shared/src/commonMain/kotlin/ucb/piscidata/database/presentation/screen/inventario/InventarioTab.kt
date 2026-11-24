package ucb.piscidata.database.presentation.screen.inventario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.domain.model.InvCategory
import ucb.piscidata.database.presentation.viewmodel.DatabaseEvent
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel

@Composable
fun InventarioTab(
    state: DatabaseState,
    viewModel: DatabaseViewModel
) {
    val filtered = state.inventory.filter {
        state.searchQuery.isBlank() ||
            it.name.contains(state.searchQuery, true) ||
            it.category.label().contains(state.searchQuery, true)
    }

    if (filtered.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Inventario vacío",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B)
                )
                Text(
                    text = "Usa «+ Agregar ítem» para registrar alimento o suministros.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filtered, key = { it.id }) { item ->
            val isLowStock = item.minQty > 0 && item.qty <= item.minQty
            val stockAccent = if (isLowStock) Color(0xFFDC2626) else Color(0xFF059669)
            val stockBg = if (isLowStock) Color(0xFFFEF2F2) else Color(0xFFECFDF5)
            val ratio = if (item.minQty > 0) {
                (item.qty.toFloat() / (item.minQty * 2f)).coerceIn(0.05f, 1f)
            } else {
                1f
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isLowStock) Color(0xFFFECACA) else Color(0xFFE5ECF2))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0B2B3B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.category.label(),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF475569)
                                    )
                                }
                                if (item.category == InvCategory.ALIMENTO) {
                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "${item.proteinPercentage ?: 40.0}% prot · ${item.pelletSizeMm ?: 2.0} mm",
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1577C8)
                                        )
                                    }
                                }
                            }
                        }

                        Surface(
                            color = stockBg,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${item.qty} ${item.unit}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = stockAccent
                            )
                        }
                    }

                    // Barra visual de nivel de stock
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = stockAccent,
                            trackColor = Color(0xFFF1F5F9)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isLowStock) "Stock bajo · Mínimo ${item.minQty} ${item.unit}"
                                else "Mínimo requerido: ${item.minQty} ${item.unit}",
                                fontSize = 11.sp,
                                fontWeight = if (isLowStock) FontWeight.Bold else FontWeight.Normal,
                                color = if (isLowStock) Color(0xFFDC2626) else Color(0xFF64748B)
                            )

                            // Control táctil de ajuste rápido (- / +)
                            Surface(
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable {
                                                if (item.qty > 0) {
                                                    viewModel.emitEvent(DatabaseEvent.UpdateInventoryQty(item.id, item.qty - 1))
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                    }
                                    VerticalDivider(
                                        modifier = Modifier.height(18.dp),
                                        color = Color(0xFFE2E8F0)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable {
                                                viewModel.emitEvent(DatabaseEvent.UpdateInventoryQty(item.id, item.qty + 1))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1577C8))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

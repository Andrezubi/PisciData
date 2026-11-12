package ucb.piscidata.database.presentation.screen.inventario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.domain.model.InvCategory
import ucb.piscidata.database.presentation.screen.components.InfoRow
import ucb.piscidata.database.presentation.viewmodel.DatabaseEvent
import ucb.piscidata.database.presentation.viewmodel.DatabaseState
import ucb.piscidata.database.presentation.viewmodel.DatabaseViewModel

@Composable
fun InventarioTab(
    state: DatabaseState,
    viewModel: DatabaseViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.inventory) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B2B3B))
                            Text(item.category.label(), fontSize = 11.sp, color = Color(0xFF6B818B))
                        }
                        Surface(
                            color = Color(0xFFE8F8F1),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${item.qty} ${item.unit}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF19A974)
                            )
                        }
                    }

                    // Protein percentage and pellet size for feed items
                    if (item.category == InvCategory.ALIMENTO) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Proteína: ${item.proteinPercentage ?: 40.0}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1577C8)
                            )
                            Text(
                                "Pellet: ${item.pelletSizeMm ?: 2.0} mm",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1577C8)
                            )
                        }
                    }

                    InfoRow("Stock mínimo requerido", "${item.minQty} ${item.unit}")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.emitEvent(DatabaseEvent.UpdateInventoryQty(item.id, item.qty - 1)) },
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.emitEvent(DatabaseEvent.UpdateInventoryQty(item.id, item.qty + 1)) },
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("+")
                        }
                    }
                }
            }
        }
    }
}

package ucb.piscidata.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ucb.piscidata.database.domain.model.FarmModel

@Composable
fun FarmSelectorHeader(
    farms: List<FarmModel>,
    selectedFarmId: Int,
    onSelectFarm: (Int) -> Unit
) {
    val currentFarmName = farms.find { it.id == selectedFarmId }?.name ?: "Piscigranja El Manantial"
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "GRANJA ACTUAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B818B),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = currentFarmName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B2B3B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1577C8)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "Cambiar ▾",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    farms.forEach { farm ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = farm.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (farm.id == selectedFarmId) FontWeight.Bold else FontWeight.Normal,
                                    color = if (farm.id == selectedFarmId) Color(0xFF1577C8) else Color(0xFF0B2B3B)
                                )
                            },
                            onClick = {
                                onSelectFarm(farm.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

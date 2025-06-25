package com.example.m_commerce_admin.features.inventory.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.m_commerce_admin.config.theme.Black
import com.example.m_commerce_admin.config.theme.Gray
import com.example.m_commerce_admin.config.theme.Teal
import com.example.m_commerce_admin.config.theme.White
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjustInventorySheet(
    visible: Boolean,
    inventoryItemId: Long?,
    onConfirm: (inventoryItemId: Long, adjustment: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var adjustmentCount by remember { mutableStateOf(0) }

    if (visible && inventoryItemId != null) {
        ModalBottomSheet(
            containerColor = White,

            onDismissRequest = {
                coroutineScope.launch { onDismiss() }
                adjustmentCount = 0
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Adjust Inventory:",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Tap + to add or – to subtract that quantity from inventory.",
                    fontSize = 12.sp,
                    color = Gray,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(colors = ButtonDefaults.buttonColors(Teal),
                        onClick = {
                            if (adjustmentCount > -1000) adjustmentCount--
                        }) {
                        Text("-")
                    }

                    Text(
                        text =
                        adjustmentCount.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (adjustmentCount < 0) Red else Black
                    )

                    Button(
                        colors = ButtonDefaults.buttonColors(Teal),
                        onClick = {
                            if (adjustmentCount < 1000) adjustmentCount++
                        }) {
                        Text("+")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(Teal),
                    onClick = {
                        onConfirm(inventoryItemId, adjustmentCount)
                        coroutineScope.launch { onDismiss() }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Adjustment")
                }
            }
        }
    }
}

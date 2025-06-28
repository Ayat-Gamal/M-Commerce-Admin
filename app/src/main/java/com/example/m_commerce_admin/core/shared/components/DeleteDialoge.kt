package com.example.m_commerce_admin.core.shared.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color.Companion.Red
import com.example.m_commerce_admin.config.theme.Teal

@Composable
fun ConfirmDeleteDialog(
    showDialog: MutableState<Boolean>,
    itemName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onDismiss()
            },
            title = {
                Text(text = "Confirm Deletion" )
            },
            text = {
                Text(text = "Are you sure you want to delete this $itemName?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                        onConfirm()
                    }
                ) {
                    Text("Delete", color =Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                        onDismiss()
                    }
                ) {
                    Text("Cancel",  color =Teal)
                }
            }
        )
    }
}


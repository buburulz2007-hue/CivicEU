package com.civiceu.com.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.civiceu.com.crypto.SecurityManager

@Composable
fun LockScreen(onUnlockSuccess: () -> Unit) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val handlePinEntry: (String) -> Unit = { digit ->
        if (pin.length < 6) {
            pin += digit
            errorMessage = ""
        }
    }

    val handleDelete: () -> Unit = {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            errorMessage = ""
        }
    }

    val handleSubmit: () -> Unit = {
        if (pin.length >= 4) {
            val result = SecurityManager.verifyPin(context, pin)
            when (result) {
                SecurityManager.PinResult.SUCCESS -> {
                    onUnlockSuccess()
                }
                SecurityManager.PinResult.DECOY -> {
                    // Mute execution of panic wipe
                    SecurityManager.executePanicWipe(context)
                    // Still log them in as if nothing happened, but app is completely empty
                    onUnlockSuccess()
                }
                SecurityManager.PinResult.FAIL -> {
                    errorMessage = "PIN incorect!"
                    pin = ""
                }
                SecurityManager.PinResult.NOT_SET -> {
                    onUnlockSuccess() // Fallback if reached accidentally
                }
            }
        }
    }

    // Numpad data
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("CLEAR", "0", "OK")
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Acces Restricționat",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Vă rugăm să introduceți PIN-ul",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // PIN Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val isFilled = i < pin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Custom Numpad
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        NumpadButton(
                            text = key,
                            onClick = {
                                when (key) {
                                    "CLEAR" -> pin = ""
                                    "OK" -> handleSubmit()
                                    else -> handlePinEntry(key)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NumpadButton(text: String, onClick: () -> Unit) {
    val isAction = text == "CLEAR" || text == "OK"
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isAction) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (isAction) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = if (isAction) 14.sp else 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

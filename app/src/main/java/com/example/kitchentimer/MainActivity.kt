package com.example.kitchentimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TimerScreen()
                }
            }
        }
    }
}

@Composable
fun TimerScreen() {
    var timeLeft by remember { mutableIntStateOf(60) }
    var isRunning by remember { mutableStateOf(false) }
    val options = listOf(60, 300, 600) // 1, 5, 10 минут в секундах

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            isRunning = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (timeLeft > 0) String.format("%02d:%02d", timeLeft / 60, timeLeft % 60) else "Время вышло!",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Ряд кнопок для выбора времени
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { seconds ->
                Button(onClick = { 
                    timeLeft = seconds
                    isRunning = false 
                }) {
                    Text("${seconds / 60} мин")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "Пауза" else "Старт")
        }
    }
}
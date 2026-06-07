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
    // Время, установленное пользователем (сохраняется между запусками)
    var selectedTime by remember { mutableIntStateOf(60) }
    // Время, которое меняется во время работы таймера
    var timeLeft by remember { mutableIntStateOf(selectedTime) }
    var isRunning by remember { mutableStateOf(false) }

    // Синхронизируем timeLeft при изменении selectedTime, если таймер не запущен
    LaunchedEffect(selectedTime) {
        if (!isRunning) timeLeft = selectedTime
    }

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

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (selectedTime > 30) selectedTime -= 30 }) {
                Text("-30с")
            }
            Text("${selectedTime / 60}:${(selectedTime % 60).toString().padStart(2, '0')}")
            Button(onClick = { selectedTime += 30 }) {
                Text("+30с")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { 
            if (!isRunning && timeLeft == 0) timeLeft = selectedTime
            isRunning = !isRunning 
        }) {
            Text(if (isRunning) "Пауза" else "Старт")
        }
    }
}
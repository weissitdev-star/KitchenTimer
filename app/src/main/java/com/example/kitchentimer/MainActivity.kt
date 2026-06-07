package com.example.kitchentimer

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    // Запрос разрешения на уведомления для Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

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
    val context = LocalContext.current
    var selectedTime by remember { mutableIntStateOf(60) }
    var timeLeft by remember { mutableIntStateOf(selectedTime) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTime) {
        if (!isRunning) timeLeft = selectedTime
    }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (isRunning && timeLeft == 0) {
            isRunning = false
            showNotification(context)
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
            Button(onClick = { if (selectedTime > 30) selectedTime -= 30 }) { Text("-30с") }
            Text("${selectedTime / 60}:${(selectedTime % 60).toString().padStart(2, '0')}")
            Button(onClick = { selectedTime += 30 }) { Text("+30с") }
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

fun showNotification(context: Context) {
    val channelId = "timer_channel"
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Таймер", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("Таймер завершен!")
        .setContentText("Ваше время истекло.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
        NotificationManagerCompat.from(context).notify(1, builder.build())
    }
}
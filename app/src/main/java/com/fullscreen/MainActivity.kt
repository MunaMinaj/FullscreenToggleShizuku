package com.example.fullscreen

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rikka.shizuku.Shizuku
import java.io.OutputStreamWriter

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission to use Shizuku if not already granted
        if (!Shizuku.isPreV11() && !Shizuku.isPermissionGranted()) {
            Shizuku.requestPermission(0)
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FullscreenToggleUI()
                }
            }
        }
    }

    @Composable
    fun FullscreenToggleUI() {
        var isFullscreen by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val success = runFullscreenCommand(isFullscreen)
                    if (success) {
                        isFullscreen = !isFullscreen
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(if (isFullscreen) "Disable Fullscreen" else "Enable Fullscreen")
            }
        }
    }

    private fun runFullscreenCommand(isCurrentlyFullscreen: Boolean): Boolean {
        if (!Shizuku.isPermissionGranted()) {
            Toast.makeText(this, "Shizuku permission not granted", Toast.LENGTH_SHORT).show()
            return false
        }

        val command = if (!isCurrentlyFullscreen)
            "wm overscan 0,0,0,-100"
        else
            "wm overscan reset"

        return try {
            val process = Shizuku.newProcess(arrayOf("sh"), null, null)
            val output = OutputStreamWriter(process.outputStream)
            output.write("$command\nexit\n")
            output.flush()
            output.close()
            process.waitFor()
            true
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
} 

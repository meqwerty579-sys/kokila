package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

import com.example.ui.designsystem.UiThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme by remember { UiThemeManager.darkTheme }
            val amoledMode by remember { UiThemeManager.amoledMode }
            val dynamicColor by remember { UiThemeManager.dynamicColor }

            MyApplicationTheme(
                darkTheme = darkTheme,
                amoledMode = amoledMode,
                dynamicColor = dynamicColor
            ) {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    SplashScreen(onSplashFinished = { showSplashScreen = false })
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        KokilaDashboard(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// UI State & Models
// -------------------------------------------------------------

enum class TabItem(val title: String, val icon: ImageVector) {
    PLAYGROUND("Playground", Icons.Rounded.PlayArrow),
    PIPELINE("Pipeline", Icons.Rounded.AccountTree),
    MODELS("Models", Icons.Rounded.Layers),
    BENCHMARK("Benchmark", Icons.Rounded.Speed),
    ARCH("Architecture", Icons.AutoMirrored.Rounded.MenuBook),
    SETTINGS("Settings", Icons.Rounded.Settings)
}

data class VoiceModelInfo(
    val id: String,
    val name: String,
    val type: String,
    val size: String,
    val status: String, // "Active", "Standby", "Downloadable"
    val language: String,
    val info: String
)

data class PipelineStep(
    val name: String,
    val durationMs: Double,
    val description: String,
    val icon: ImageVector
)

// -------------------------------------------------------------
// Dashboard Composable
// -------------------------------------------------------------

@Composable
fun KokilaDashboard(modifier: Modifier = Modifier) {
    var activeTab by remember { mutableStateOf(TabItem.PLAYGROUND) }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Header
        AppHeader()

        // Main Tab Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                TabItem.PLAYGROUND -> PlaygroundScreen()
                TabItem.PIPELINE -> PipelineScreen()
                TabItem.MODELS -> ModelsScreen()
                TabItem.BENCHMARK -> BenchmarkScreen()
                TabItem.ARCH -> ArchitectureScreen()
                TabItem.SETTINGS -> SettingsScreen()
            }
        }

        // Custom Navigation Bar for premium look
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            TabItem.values().forEach { tab ->
                val selected = activeTab == tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { activeTab = tab },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    label = {
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                )
            }
        }
    }
}

@Composable
fun AppHeader() {
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = borderColor, // Dynamic dynamic theme-supported border color
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sleek Blue Status Indicator for Kokila TTS Status
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "KOKILA OFFLINE TTS",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Secure On-Device Voice Engine",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp
            )
        }
    }
}

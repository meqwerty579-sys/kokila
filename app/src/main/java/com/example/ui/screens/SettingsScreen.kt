package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.KokilaApplication
import com.example.ui.designsystem.Spacing
import com.example.ui.designsystem.UiThemeManager
import com.example.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = (LocalContext.current.applicationContext as KokilaApplication).appContainer.viewModelFactory
    )
) {
    val context = LocalContext.current
    val crashLogs by viewModel.crashLogs.collectAsStateWithLifecycle()
    var showLogsDialog by remember { mutableStateOf(false) }

    // Theme states from our design system theme manager
    var darkThemeEnabled by remember { UiThemeManager.darkTheme }
    var amoledModeEnabled by remember { UiThemeManager.amoledMode }
    var dynamicColorsEnabled by remember { UiThemeManager.dynamicColor }

    // UI-only interactive settings
    var monoOutput by remember { mutableStateOf(false) }
    var hapticFeedbackEnabled by remember { mutableStateOf(true) }
    var backgroundPreloadEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.l),
        contentPadding = PaddingValues(bottom = Spacing.xxl, top = Spacing.s)
    ) {
        // Redesigned Header
        item {
            Column(modifier = Modifier.padding(vertical = Spacing.m)) {
                Text(
                    text = "System Settings",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Configure speech engine, accessibility parameters, diagnostics logs, and look & feel.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // CATEGORY 1: Visual Styling & Theme (M3 Expressive, Dark, AMOLED Mode)
        item {
            SettingsGroup(title = "Visual & Styling Core", icon = Icons.Rounded.Palette) {
                // Dark Theme switch
                SettingsSwitchRow(
                    title = "Force Dark Mode Theme",
                    description = "Overrides the system theme to slate-neutral dark background spaces.",
                    checked = darkThemeEnabled,
                    onCheckedChange = { darkThemeEnabled = it },
                    icon = Icons.Rounded.DarkMode
                )
                
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // AMOLED True Black switch (only available if Dark Mode is active)
                SettingsSwitchRow(
                    title = "True Black AMOLED Mode",
                    description = "Reduces organic LED pixel light emission to conserve phone battery life.",
                    checked = amoledModeEnabled,
                    onCheckedChange = { amoledModeEnabled = it },
                    enabled = darkThemeEnabled,
                    icon = Icons.Rounded.BatteryChargingFull
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Dynamic Material 3 Colors
                SettingsSwitchRow(
                    title = "Dynamic M3 Colors",
                    description = "Syncs Kokila colors with your active system wallpaper design palette (Android 12+).",
                    checked = dynamicColorsEnabled,
                    onCheckedChange = { dynamicColorsEnabled = it },
                    icon = Icons.Rounded.ColorLens
                )
            }
        }

        // CATEGORY 2: Audio & Synthesis Settings
        item {
            SettingsGroup(title = "Acoustic Synthesizer Mode", icon = Icons.Rounded.VolumeUp) {
                SettingsSwitchRow(
                    title = "Force Mono Track",
                    description = "Saves AudioTrack bandwidth by forcing native sound streams down 16kHz mono buffers.",
                    checked = monoOutput,
                    onCheckedChange = { monoOutput = it },
                    icon = Icons.Rounded.Hearing
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                SettingsSwitchRow(
                    title = "Background Model Preloading",
                    description = "Pre-loads Piper weight files into system RAM for ultra-low latency generation.",
                    checked = backgroundPreloadEnabled,
                    onCheckedChange = { backgroundPreloadEnabled = it },
                    icon = Icons.Rounded.Memory
                )
            }
        }

        // CATEGORY 3: Accessibility Settings
        item {
            SettingsGroup(title = "Accessibility Engine", icon = Icons.Rounded.SettingsAccessibility) {
                SettingsSwitchRow(
                    title = "Synthesis Haptics Vibe",
                    description = "Triggers rhythmic micro-vibrations corresponding to active speech cycles.",
                    checked = hapticFeedbackEnabled,
                    onCheckedChange = { hapticFeedbackEnabled = it },
                    icon = Icons.Rounded.Vibration
                )
            }
        }

        // CATEGORY 4: Diagnostics & Local Log Manager
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.l)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        Icon(
                            Icons.Rounded.BugReport,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Diagnostics & System Logs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.s))
                    
                    Text(
                        text = "Examine local Native C++ / eSpeak traces and stack logs in real time. Private, offline, and kept strictly inside sandboxed spaces.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(Spacing.l))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        Button(
                            onClick = {
                                viewModel.refreshLogs()
                                showLogsDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .testTag("export_logs_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Icon(Icons.Rounded.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Inspect Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.clearLogs()
                                Toast.makeText(context, "Local diagnostics flushed.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("clear_logs_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Icon(Icons.Rounded.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                Text("Flush Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // CATEGORY 5: About & Privacy
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.l),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Rounded.VerifiedUser,
                        contentDescription = "Zero Telemetry Guarantee",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.m))
                    Column {
                        Text(
                            text = "Zero Cloud-Telemetry Assurance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "Kokila contains no analytics tracking scripts, internet-calling cookies, or marketing trackers. Text compilation and PCM sound waves are rendered purely on your device's physical CPU registers.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Version Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Spacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KOKILA OFFLINE ENTERPRISE SUITE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Release Version 1.2.0-Production",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }

    // Modal Logs Dialog Redesigned
    if (showLogsDialog) {
        AlertDialog(
            onDismissRequest = { showLogsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                ) {
                    Icon(Icons.Rounded.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Local Stack Trace Log",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .padding(Spacing.s)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = if (crashLogs.isEmpty()) "No error logs found. Engine running perfectly." else crashLogs,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Kokila Diagnostic Logs", crashLogs)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Logs copied.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Copy Stack", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogsDialog = false }
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// Group settings layout container Helper Composable
@Composable
fun SettingsGroup(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Spacing.m)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                modifier = Modifier.padding(bottom = Spacing.s, start = Spacing.xs, top = Spacing.xs)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

// Helper switch settings line Composable
@Composable
fun SettingsSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(alpha = if (enabled) 1f else 0.5f)
            .padding(vertical = Spacing.s, horizontal = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.m)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

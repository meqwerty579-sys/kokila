package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.PipelineStep
import com.example.ui.designsystem.Spacing
import kotlinx.coroutines.delay

@Composable
fun PipelineScreen() {
    val steps = listOf(
        PipelineStep("Text Normalization (Frontend)", 0.4, "Cleans raw UTF-8 input, expands acronyms, standardizes integers/decimals, and standardizes localized time formats.", Icons.Rounded.Spellcheck),
        PipelineStep("Grapheme to Phoneme (G2P)", 0.8, "Converts standardized textual tokens into discrete IPA linguistic phonetic vectors using eSpeak-ng mapping tables.", Icons.Rounded.Translate),
        PipelineStep("Acoustic Model Inference (VITS)", 10.5, "Feeds phoneme sequence matrices directly into local ONNX Runtime threads to output speaker spectral spectrograms.", Icons.Rounded.Psychology),
        PipelineStep("Neural Vocoding (HiFi-GAN)", 3.4, "Converts computed speaker spectrum spectrogram maps directly into physical 16kHz PCM audio waveforms.", Icons.Rounded.GraphicEq),
        PipelineStep("JNI Buffer Mixer & AudioTrack", 0.9, "Pins 16-bit JNI ByteBuffers and pushes compiled audio frames directly into the system AudioTrack hardware loop.", Icons.Rounded.VolumeUp)
    )

    var activeStepIdx by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            for (i in steps.indices) {
                activeStepIdx = i
                delay(2200)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.l),
        contentPadding = PaddingValues(bottom = Spacing.xxl, top = Spacing.s)
    ) {
        // Redesigned Pipeline Header
        item {
            Column(modifier = Modifier.padding(vertical = Spacing.m)) {
                Text(
                    text = "Secure Processing Pipeline",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "A live, isolated trace of JNI registers compiling text and pushing PCM speech frames to physical device speakers.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live JNI Registers overview card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.l)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                        ) {
                            Icon(Icons.Rounded.Terminal, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Thread Registry Diagnostics",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("THREAD OK", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.m))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ACTIVE WORKER", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("ThreadID: #4028", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MEM ALLOCATOR", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Native JNI DMA", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text("COMPILER LATENCY", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("15.2ms Total", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Live Steps Connector Timeline Nodes
        items(steps.size) { index ->
            val step = steps[index]
            val isActive = index == activeStepIdx

            val borderGlowColor by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                animationSpec = tween(400)
            )

            val backgroundTint by animateColorAsState(
                targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else Color.Transparent,
                animationSpec = tween(400)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(alpha = if (isActive) 1.0f else 0.72f),
                horizontalArrangement = Arrangement.spacedBy(Spacing.m)
            ) {
                // Left Timeline Connector
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    // Pulsing Timeline Circle
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.5.dp,
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isActive) {
                            val pulseTransition = rememberInfiniteTransition(label = "pulse")
                            val pulseScale by pulseTransition.animateFloat(
                                initialValue = 0.5f,
                                targetValue = 1.0f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulse"
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.45f))
                            )
                        }
                        Icon(
                            imageVector = if (isActive) Icons.Rounded.PlayArrow else Icons.Rounded.Check,
                            contentDescription = null,
                            tint = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    // Vertical connection line (draw only if not last item)
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(94.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = if (isActive) {
                                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.outlineVariant)
                                        } else {
                                            listOf(MaterialTheme.colorScheme.outlineVariant, MaterialTheme.colorScheme.outlineVariant)
                                        }
                                    )
                                )
                        )
                    }
                }

                // Right detailed card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.2.dp, borderGlowColor),
                    modifier = Modifier
                        .weight(1f)
                        .background(backgroundTint, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(Spacing.m)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.s)
                            ) {
                                Icon(
                                    imageVector = step.icon,
                                    contentDescription = null,
                                    tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = step.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${step.durationMs}ms",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.s))

                        Text(
                            text = step.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        // Animated thread progression bar inside active step
                        if (isActive) {
                            Spacer(modifier = Modifier.height(Spacing.m))
                            
                            val infiniteOffset = rememberInfiniteTransition(label = "bar")
                            val barProgress by infiniteOffset.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(2200, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "progress"
                            )
                            
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Compiling native buffers...", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("ACTIVE THREAD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                LinearProgressIndicator(
                                    progress = { barProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.designsystem.Spacing

@Composable
fun ArchitectureScreen() {
    val context = LocalContext.current
    
    val phases = listOf(
        Pair("Phase 1 — Working System TTS", "Compile direct JNI bindings from sherpa-onnx utilizing NDK toolchains. Bundle compact 8MB Piper voices inside the application assets module. Map the service inside AndroidManifest with BIND_TEXT_TO_SPEECH permissions and standard intent filters to expose Kokila inside the system TTS selection menus."),
        Pair("Phase 2 — Pipeline Threading Control", "Manage native speech synthesizer cycles in independent C++ execution contexts. Avoid GC pauses and allocation delays by implementing zero-overhead direct native byte-buffers linked directly to the SynthesisCallback stream. Monitor interruptions gracefully with atomic cancellation states."),
        Pair("Phase 3 — Footprint Size Optimization", "Optimize storage footprints by leveraging custom compilations of the ONNX Runtime that compile only required operators. Set noCompress 'onnx' in the Gradle build configuration to allow zero-copy memory mapping, optimizing the physical RAM footprint on standard hardware."),
        Pair("Phase 4 — Model Architecture Choice", "Incorporate the premium Kokoro voice compiled with ExecuTorch model backends for studio-grade audio narration tasks. Support optional hardware neural engine (NNAPI) compilation blocks to reduce overall thermal and battery overheads during continuous reading operations."),
        Pair("Phase 5 — Regional Localization", "Support regional and Telugu dialects by fine-tuning VITS text models against custom databases like the Swecha Telugu corpus. Construct customized phonemic grapheme-to-phoneme maps inside espeak-ng dictionaries for accurate regional vowel and accent synthesis.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Spacing.l),
        contentPadding = PaddingValues(bottom = Spacing.xxl, top = Spacing.s)
    ) {
        // Redesigned Architecture Header
        item {
            Column(modifier = Modifier.padding(vertical = Spacing.m)) {
                Text(
                    text = "System Architecture Spec",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Engineering roadmap phases and build configurations for low-latency, private, offline voice synthesis on edge hardware.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Phases Roadmaps Items
        items(phases) { (title, description) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Spacing.l)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
                        modifier = Modifier.padding(bottom = Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
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
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = Spacing.xs))
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Terminal Build Config Card
        item {
            val codeSnippet = "android {\n" +
                    "    aaptOptions {\n" +
                    "        noCompress 'onnx'\n" +
                    "    }\n" +
                    "}"

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Slate Terminal Dark
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
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
                            // Mock macOS terminal control buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFEF4444)))
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF59E0B)))
                                Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF10B981)))
                            }
                            Spacer(modifier = Modifier.width(Spacing.s))
                            Text(
                                text = "build.gradle.kts",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Copy button
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Gradle NoCompress Config", codeSnippet)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Code copied.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = "Copy snippet",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.m))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(Spacing.m)
                    ) {
                        Text(
                            text = codeSnippet,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8), // Code blue highlight
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.m))

                    Text(
                        text = "Preventing AAPT compression on ONNX model weights allows the application to directly memory-map files directly from the APK, reducing the launch time RAM block allocation by up to 80%.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

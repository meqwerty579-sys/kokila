package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.designsystem.Spacing
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    
    // Wave pulse and rotate animations
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    // Fade-in entry animation for text
    var textAlpha by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(500, easing = LinearOutSlowInEasing)
        ) { value, _ ->
            textAlpha = value
        }
        delay(950) // Premium fast splash (< 1 second)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(Spacing.xl)
        ) {
            // Animated Voice Intelligence Core
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer(
                        scaleX = pulseScale,
                        scaleY = pulseScale
                    )
            ) {
                // Background Glowing Ring
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.35f),
                            Color(0xFF14B8A6).copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width / 2
                    )
                    drawCircle(brush = brush, radius = size.width / 2)
                }

                // Orbiting particles ring
                Canvas(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer(rotationZ = rotateAngle)
                ) {
                    val numPoints = 6
                    val radius = size.width / 2.2f
                    for (i in 0 until numPoints) {
                        val angle = (i * 2 * Math.PI / numPoints)
                        val x = center.x + radius * kotlin.math.cos(angle).toFloat()
                        val y = center.y + radius * sin(angle).toFloat()
                        drawCircle(
                            color = if (i % 2 == 0) Color(0xFF6366F1) else Color(0xFF14B8A6),
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                // Central high-contrast AI Voice icon
                Icon(
                    imageVector = Icons.Rounded.RecordVoiceOver,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Brand Title & Subtitle with animated entry
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            ) {
                Text(
                    text = "Kokila",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-1).sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                Text(
                    text = "Private Offline Voice Intelligence",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            // Pure-compose live wave simulator footer
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(24.dp)
                    .graphicsLayer(alpha = textAlpha),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val midY = height / 2f
                    val points = 30
                    val step = width / points
                    
                    val time = System.currentTimeMillis() / 250f
                    
                    for (p in 0..1) {
                        val pathBrush = Brush.linearGradient(
                            colors = if (p == 0) {
                                listOf(Color(0xFF6366F1), Color(0xFF14B8A6))
                            } else {
                                listOf(Color(0xFF14B8A6), Color(0xFF8B5CF6))
                            }
                        )
                        for (i in 0 until points - 1) {
                            val x1 = i * step
                            val angle1 = (i * 0.25) + time + (p * Math.PI / 2)
                            val y1 = midY + (sin(angle1) * 8f * pulseScale).toFloat()

                            val x2 = (i + 1) * step
                            val angle2 = ((i + 1) * 0.25) + time + (p * Math.PI / 2)
                            val y2 = midY + (sin(angle2) * 8f * pulseScale).toFloat()

                            drawLine(
                                brush = pathBrush,
                                start = Offset(x1, y1),
                                end = Offset(x2, y2),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}

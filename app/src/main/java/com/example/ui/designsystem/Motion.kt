package com.example.ui.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

object Motion {
    // Easing curves compatible with Material 3 Expressive Design
    val Emphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val StandardDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
    val StandardAccelerate = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)

    // Standard durations (ms)
    object Duration {
        const val Short1 = 50
        const val Short2 = 100
        const val Short3 = 150
        const val Short4 = 200
        const val Medium1 = 250
        const val Medium2 = 300
        const val Medium3 = 350
        const val Medium4 = 400
        const val Long1 = 450
        const val Long2 = 500
        const val Long3 = 550
        const val Long4 = 600
    }

    fun <T> tweenShort(duration: Int = Duration.Short4) = tween<T>(
        durationMillis = duration,
        easing = StandardDecelerate
    )

    fun <T> tweenMedium(duration: Int = Duration.Medium2) = tween<T>(
        durationMillis = duration,
        easing = EmphasizedDecelerate
    )

    fun <T> tweenLong(duration: Int = Duration.Long2) = tween<T>(
        durationMillis = duration,
        easing = Emphasized
    )
}

package com.bitbytestudio.customanalogclock

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

//@Composable
//fun CustomAnalogClock() {
//    val infiniteTransition = rememberInfiniteTransition(label = "")
//
//    // Animating the seconds hand for smooth rotation
//    val secondHandAngle by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 60000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        ), label = ""
//    )
//
//    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//        val centerX = canvasWidth / 2
//        val centerY = canvasHeight / 2
//        val radius = min(centerX, centerY) - 20.dp.toPx()
//
//        // Draw clock face
//        drawCircle(color = Color.Black, radius = radius, center = Offset(centerX, centerY), style = Stroke(width = 5f))
//
//        // Get current time
//        val calendar = Calendar.getInstance()
//        val hours = calendar.get(Calendar.HOUR)
//        val minutes = calendar.get(Calendar.MINUTE)
//        val seconds = calendar.get(Calendar.SECOND)
//
//        // Calculate angles
//        val hourAngle = (hours + minutes / 60f) * 30f // 360° / 12 = 30° per hour
//        val minuteAngle = (minutes + seconds / 60f) * 6f // 360° / 60 = 6° per minute
//        val adjustedSecondAngle = secondHandAngle + (seconds % 60) * 6f // Smooth second hand angle
//
//        // Draw hour hand
//        drawHand(centerX, centerY, radius * 0.5f, hourAngle, Color.Black, 8f)
//
//        // Draw minute hand
//        drawHand(centerX, centerY, radius * 0.7f, minuteAngle, Color.Black, 6f)
//
//        // Draw second hand
//        drawHand(centerX, centerY, radius * 0.9f, adjustedSecondAngle, Color.Red, 4f)
//    }
//}
//
//fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHand(
//    cx: Float,
//    cy: Float,
//    length: Float,
//    angle: Float,
//    color: Color,
//    strokeWidth: Float
//) {
//    val radian = Math.toRadians(angle.toDouble() - 90)
//    val x = cx + length * cos(radian).toFloat()
//    val y = cy + length * sin(radian).toFloat()
//
//    drawLine(
//        start = Offset(cx, cy),
//        end = Offset(x, y),
//        color = color,
//        strokeWidth = strokeWidth,
//        cap = StrokeCap.Round
//    )
//}


@Composable
fun CustomAnalogClock() {
    val currentTime = remember { mutableStateOf(Calendar.getInstance()) }

    // Smoothly update the clock time at ~90fps
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = Calendar.getInstance()
            delay(11L) // 11ms for ~90fps animation
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val canvasWidth = constraints.maxWidth.toFloat()
        val canvasHeight = constraints.maxHeight.toFloat()
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2
        val radius = with(LocalDensity.current) {
            min(centerX, centerY) - 20.dp.toPx()
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            // Draw clock face
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 5f)
            )

            // Draw clock ticks and numbers
            for (i in 1..12) {
                val angle = Math.toRadians(i * 30.0 - 90) // Position each number
                val numberRadius = radius - 30.dp.toPx()
                val numberX = centerX + numberRadius * cos(angle).toFloat()
                val numberY = centerY + numberRadius * sin(angle).toFloat()

                // Draw the number
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        i.toString(),
                        numberX,
                        numberY + 12,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 40f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                // Draw clock ticks
                val tickStartX = centerX + (radius - 10.dp.toPx()) * cos(angle).toFloat()
                val tickStartY = centerY + (radius - 10.dp.toPx()) * sin(angle).toFloat()
                val tickEndX = centerX + radius * cos(angle).toFloat()
                val tickEndY = centerY + radius * sin(angle).toFloat()

                drawLine(
                    color = Color.Black,
                    start = Offset(tickStartX, tickStartY),
                    end = Offset(tickEndX, tickEndY),
                    strokeWidth = 3f
                )
            }
        }

        // Extract the current time values
        val hours = currentTime.value.get(Calendar.HOUR)
        val minutes = currentTime.value.get(Calendar.MINUTE)
        val seconds = currentTime.value.get(Calendar.SECOND)
        val millis = currentTime.value.get(Calendar.MILLISECOND)

        // Calculate angles with millisecond precision
        val hourAngle = (hours + minutes / 60f) * 30f
        val minuteAngle = (minutes + seconds / 60f) * 6f
        val secondAngle = (seconds + millis / 1000f) * 6f

        // Draw clock hands using the correct center
        CustomClockHand(centerX = centerX, centerY = centerY, length = radius * 0.5f, angle = hourAngle, color = Color.Black)
        CustomClockHand(centerX = centerX, centerY = centerY, length = radius * 0.75f, angle = minuteAngle, color = Color.Gray)
        CustomClockHand(centerX = centerX, centerY = centerY, length = radius * 0.9f, angle = secondAngle, color = Color.Red)
    }
}



@Composable
fun CustomClockHand(
    centerX: Float,
    centerY: Float,
    length: Float,
    angle: Float,
    color: Color,
    strokeWidth: Float = 10f,
    handType: HandType = HandType.STANDARD
) {
    val radian = Math.toRadians(angle.toDouble() - 90)
    val xOffset = length * cos(radian).toFloat()
    val yOffset = length * sin(radian).toFloat()

    Canvas(modifier = Modifier.fillMaxSize()) {
        when (handType) {
            HandType.STANDARD -> {
                // Draw a simple line with rounded ends
                drawLine(
                    color = color,
                    start = Offset(centerX, centerY),
                    end = Offset(centerX + xOffset, centerY + yOffset),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            HandType.GRADIENT -> {
                // Create a gradient brush
                val gradient = Brush.linearGradient(
                    colors = listOf(color, Color.Transparent),
                    start = Offset(centerX, centerY),
                    end = Offset(centerX + xOffset, centerY + yOffset)
                )
                drawLine(
                    brush = gradient,
                    start = Offset(centerX, centerY),
                    end = Offset(centerX + xOffset, centerY + yOffset),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            HandType.TAPERED -> {
                // Draw a tapered path
                val path = Path().apply {
                    moveTo(centerX, centerY)
                    lineTo(centerX + xOffset, centerY + yOffset)
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }

            HandType.SHADOWED -> {
                // Add shadow effect
                drawLine(
                    color = color.copy(alpha = 0.7f),
                    start = Offset(centerX + 5, centerY + 5), // Offset shadow
                    end = Offset(centerX + xOffset + 5, centerY + yOffset + 5),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(centerX, centerY),
                    end = Offset(centerX + xOffset, centerY + yOffset),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}


enum class HandType {
    STANDARD, GRADIENT, TAPERED, SHADOWED
}




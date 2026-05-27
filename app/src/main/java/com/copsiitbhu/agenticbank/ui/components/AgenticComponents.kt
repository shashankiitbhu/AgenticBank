package com.copsiitbhu.agenticbank.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.copsiitbhu.agenticbank.agent.AgentLog
import com.copsiitbhu.agenticbank.agent.LogType

// ---------------------------------------------------------------------------
// Design tokens
// ---------------------------------------------------------------------------

object AgenticColors {
    val Background = Color(0xFFFDFDFB)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF9F9F7)
    val CardBorder = Color(0xFFE8E8E1)
    val Accent = Color(0xFF1A1A1A)
    val AccentSecondary = Color(0xFF4A4A46)
    val AccentSuccess = Color(0xFF4CAF50)
    val AccentWarning = Color(0xFFFF9800)
    val TextPrimary = Color(0xFF1A1A1A)
    val TextSecondary = Color(0xFF6A6A66)
    val TextMuted = Color(0xFF9A9A95)
    val GradientStart = Color(0xFF1A1A1A)
    val GradientEnd = Color(0xFF4A4A46)
    val HighlightGlow = Color(0xFF1A1A1A).copy(alpha = 0.08f)
    val DebitRed = Color(0xFFD32F2F)
    val CreditGreen = Color(0xFF388E3C)
}

val GradientBrush = Brush.linearGradient(
    colors = listOf(AgenticColors.GradientStart, AgenticColors.AccentSecondary)
)

// ---------------------------------------------------------------------------
// Glowing border modifier
// ---------------------------------------------------------------------------

@Composable
fun Modifier.glowingBorder(
    glowColor: Color = AgenticColors.Accent,
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp
): Modifier = this
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            listOf(glowColor.copy(alpha = 0.8f), glowColor.copy(alpha = 0.2f), glowColor.copy(alpha = 0.8f))
        ),
        shape = RoundedCornerShape(cornerRadius)
    )

// ---------------------------------------------------------------------------
// Animated highlight border for agent-controlled fields
// ---------------------------------------------------------------------------

@Composable
fun Modifier.agentHighlight(active: Boolean): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "highlight")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    return if (active) {
        this.border(
            width = 2.dp,
            brush = Brush.linearGradient(
                listOf(
                    AgenticColors.Accent.copy(alpha = alpha),
                    AgenticColors.AccentSecondary.copy(alpha = alpha)
                )
            ),
            shape = RoundedCornerShape(14.dp)
        )
    } else {
        this.border(1.dp, AgenticColors.CardBorder, RoundedCornerShape(14.dp))
    }
}

// ---------------------------------------------------------------------------
// Pulsing dot indicator
// ---------------------------------------------------------------------------

@Composable
fun PulsingDot(
    modifier: Modifier = Modifier,
    color: Color = AgenticColors.Accent,
    size: Dp = 10.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(size * 1.8f),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * scale)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha * 0.3f))
        )
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color)
        )
    }
}

// ---------------------------------------------------------------------------
// Agent log panel
// ---------------------------------------------------------------------------

@Composable
fun AgentLogPanel(
    logs: List<AgentLog>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AgenticColors.Surface)
            .border(1.dp, AgenticColors.CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulsingDot(size = 7.dp, color = AgenticColors.AccentSuccess)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "AGENT CONSOLE",
                color = AgenticColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
        Spacer(Modifier.height(12.dp))
        AnimatedVisibility(visible = logs.isEmpty()) {
            Text(
                text = "Awaiting instructions…",
                color = AgenticColors.TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        logs.takeLast(6).forEach { log ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + slideInVertically { it / 2 }
            ) {
                val logColor = when (log.type) {
                    LogType.ACTION -> AgenticColors.Accent
                    LogType.SUCCESS -> AgenticColors.AccentSuccess
                    LogType.ERROR -> AgenticColors.DebitRed
                    LogType.INFO -> AgenticColors.TextSecondary
                }
                Text(
                    text = log.message,
                    color = logColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Gradient text
// ---------------------------------------------------------------------------

@Composable
fun GradientText(
    text: String,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Bold,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        style = LocalTextStyle.current.copy(
            brush = Brush.linearGradient(
                listOf(AgenticColors.GradientStart, AgenticColors.AccentSecondary)
            )
        ),
        modifier = modifier
    )
}

// ---------------------------------------------------------------------------
// Section header
// ---------------------------------------------------------------------------

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        color = AgenticColors.TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = modifier
    )
}

// ---------------------------------------------------------------------------
// Animated counter (for balance reveal)
// ---------------------------------------------------------------------------

@Composable
fun AnimatedBalance(targetBalance: String) {
    var displayed by remember { mutableStateOf("₹0.00") }
    LaunchedEffect(targetBalance) {
        displayed = targetBalance
    }
    GradientText(
        text = displayed,
        fontSize = 36.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

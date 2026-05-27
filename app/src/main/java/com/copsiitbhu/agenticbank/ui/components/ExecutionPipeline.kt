package com.copsiitbhu.agenticbank.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copsiitbhu.agenticbank.agent.AgentType

@Composable
fun ExecutionPipeline(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val steps = AgentType.values()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            PipelineStep(
                label = step.name.lowercase().replaceFirstChar { it.uppercase() },
                isActive = index == currentStep,
                isCompleted = index < currentStep
            )
            
            if (index < steps.size - 1) {
                PipelineConnector(isCompleted = index < currentStep)
            }
        }
    }
}

@Composable
private fun PipelineStep(
    label: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val color by animateColorAsState(
        targetValue = when {
            isActive -> Color(0xFF1A1A1A)
            isCompleted -> Color(0xFF4CAF50)
            else -> Color(0xFFD1D1CB)
        },
        label = "color"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .scale(if (isActive) scale else 1f)
                .background(color, CircleShape)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) Color(0xFF1A1A1A) else Color(0xFF9A9A95),
            fontSize = 9.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun PipelineConnector(isCompleted: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isCompleted) Color(0xFF4CAF50) else Color(0xFFE8E8E1),
        label = "connector"
    )
    
    Box(
        modifier = Modifier
            .height(2.dp)
            .width(40.dp)
            .background(color)
    )
}

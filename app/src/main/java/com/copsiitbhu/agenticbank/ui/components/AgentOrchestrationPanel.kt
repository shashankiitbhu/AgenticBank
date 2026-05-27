package com.copsiitbhu.agenticbank.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copsiitbhu.agenticbank.agent.AgentStatus
import com.copsiitbhu.agenticbank.agent.AgentType

@Composable
fun AgentOrchestrationPanel(
    agentStatuses: Map<AgentType, AgentStatus>,
    activeAgent: AgentType?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE8E8E1), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "AGENT ORCHESTRATION",
            color = Color(0xFF9A9A95),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
        Spacer(Modifier.height(12.dp))

        AgentType.values().forEach { type ->
            AgentRow(
                agentName = type.displayName,
                status = agentStatuses[type] ?: AgentStatus.IDLE,
                isActive = type == activeAgent
            )
        }
    }
}

@Composable
private fun AgentRow(
    agentName: String,
    status: AgentStatus,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (icon, color) = when (status) {
            AgentStatus.IDLE -> Icons.Rounded.Info to Color(0xFFD1D1CB)
            AgentStatus.ACTIVE -> Icons.Rounded.Info to Color(0xFF1A1A1A)
            AgentStatus.SUCCESS -> Icons.Rounded.CheckCircle to Color(0xFF4CAF50)
            AgentStatus.ERROR -> Icons.Rounded.Warning to Color(0xFFD32F2F)
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(Modifier.width(12.dp))

        Text(
            text = agentName,
            color = if (isActive) Color(0xFF1A1A1A) else Color(0xFF9A9A95),
            fontSize = 13.sp,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        if (isActive) {
            PulsingDot(size = 6.dp, color = Color(0xFF1A1A1A))
        }
    }
}

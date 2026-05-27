package com.copsiitbhu.agenticbank.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DynamicUiRenderer(
    modules: List<DynamicUiModule>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        modules.forEach { module ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(600)) + expandVertically(tween(600)) + slideInVertically { it / 2 },
                exit = fadeOut(tween(400)) + shrinkVertically(tween(400))
            ) {
                DynamicModuleContainer(module = module)
            }
        }
    }
}

@Composable
private fun DynamicModuleContainer(module: DynamicUiModule) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE8E8E1), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        when (module) {
            is DynamicUiModule.SplitPayment -> RenderSplitPayment(module)
            is DynamicUiModule.RecurringPayment -> RenderRecurringPayment(module)
            is DynamicUiModule.ReminderTimeline -> RenderReminderTimeline(module)
            is DynamicUiModule.ApprovalWorkflow -> RenderApprovalWorkflow(module)
            is DynamicUiModule.ExpenseInsight -> RenderExpenseInsight(module)
            is DynamicUiModule.FraudBanner -> RenderFraudBanner(module)
            is DynamicUiModule.ParticipantList -> RenderParticipantList(module)
            is DynamicUiModule.SmartRecommendation -> RenderSmartRecommendation(module)
        }
    }
}

@Composable
private fun RenderSplitPayment(data: DynamicUiModule.SplitPayment) {
    Column {
        Text("SPLIT SETTLEMENT", color = Color(0xFF9A9A95), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("₹${data.totalAmount}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
            Spacer(Modifier.weight(1f))
            Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF9F9F7)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Text("${data.participants.size} People", color = Color(0xFF1A1A1A), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Each person pays ₹${data.perPersonAmount}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
            data.participants.forEach { p ->
                ParticipantAvatar(p)
            }
        }
    }
}

@Composable
private fun ParticipantAvatar(p: Participant) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFFF0F0EE))
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(p.avatar, fontSize = 16.sp)
    }
}

@Composable
private fun RenderRecurringPayment(data: DynamicUiModule.RecurringPayment) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("RECURRING PLAN", color = Color(0xFF9A9A95), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("₹${data.amount} / ${data.frequency}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Next: ${data.nextDate}", color = Color(0xFF6A6A66), fontSize = 12.sp)
        }
        Icon(Icons.Rounded.Refresh, null, tint = Color(0xFF1A1A1A))
    }
}

@Composable
private fun RenderReminderTimeline(data: DynamicUiModule.ReminderTimeline) {
    Column {
        Text("PAYMENT TIMELINE", color = Color(0xFF9A9A95), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        data.events.forEachIndexed { index, event ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(if(event.isCompleted) Color(0xFF4CAF50) else Color(0xFFD1D1CB), CircleShape))
                Spacer(Modifier.width(12.dp))
                Text(event.title, fontSize = 13.sp, color = if(event.isCompleted) Color(0xFF9A9A95) else Color(0xFF1A1A1A))
                Spacer(Modifier.weight(1f))
                Text(event.date, fontSize = 11.sp, color = Color(0xFF9A9A95))
            }
            if (index < data.events.size - 1) {
                Box(Modifier.padding(start = 3.dp).width(1.dp).height(16.dp).background(Color(0xFFE8E8E1)))
            }
        }
    }
}

@Composable
private fun RenderApprovalWorkflow(data: DynamicUiModule.ApprovalWorkflow) {
    Column {
        Text("APPROVAL FLOW", color = Color(0xFF9A9A95), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            data.steps.forEach { step ->
                Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(if(step.status == "DONE") Color(0xFF4CAF50).copy(0.1f) else Color(0xFFF9F9F7)).padding(8.dp)) {
                    Text(step.label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if(step.status == "DONE") Color(0xFF4CAF50) else Color(0xFF6A6A66))
                }
            }
        }
    }
}

@Composable
private fun RenderExpenseInsight(data: DynamicUiModule.ExpenseInsight) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("AI INSIGHT", color = Color(0xFF1A1A1A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("${data.category} is ${data.percentage} of your spend", fontSize = 14.sp)
            Text(data.trend, color = Color(0xFFD32F2F), fontSize = 11.sp)
        }
        Box(Modifier.size(40.dp).background(Color(0xFFF9F9F7), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Search, null, tint = Color(0xFF1A1A1A), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun RenderFraudBanner(data: DynamicUiModule.FraudBanner) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF3F3)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Warning, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(12.dp))
        Text(data.message, color = Color(0xFFD32F2F), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RenderParticipantList(data: DynamicUiModule.ParticipantList) {
    Column {
        Text("PARTICIPANTS", color = Color(0xFF9A9A95), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        data.people.forEach { p ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                ParticipantAvatar(p)
                Spacer(Modifier.width(12.dp))
                Text(p.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                if (p.amount != null) Text("₹${p.amount}", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RenderSmartRecommendation(data: DynamicUiModule.SmartRecommendation) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Info, null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("SUGGESTION", color = Color(0xFFFFA000), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(data.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(data.description, color = Color(0xFF6A6A66), fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        Text(data.actionText, color = Color(0xFF1A1A1A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

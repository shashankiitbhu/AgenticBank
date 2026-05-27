package com.copsiitbhu.agenticbank.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StreamingReasoningPanel(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F7))
            .border(
                width = 1.dp, 
                color = Color(0xFFE8E8E1), 
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulsingDot(size = 6.dp, color = Color(0xFF1A1A1A))
            Spacer(Modifier.width(10.dp))
            Text(
                text = "SYSTEM REASONING",
                color = Color(0xFF1A1A1A),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
        
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(logs) { index, log ->
                val isLast = index == logs.size - 1
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400)) + expandVertically(tween(400))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isLast) Color(0xFF1A1A1A).copy(alpha = 0.03f) else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = if (log.startsWith("✗")) "!" else ">",
                            color = if (log.startsWith("✗")) Color(0xFFD32F2F) else Color(0xFF1A1A1A).copy(alpha = 0.4f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = log,
                            color = if (log.startsWith("✗")) Color(0xFFD32F2F) else Color(0xFF1A1A1A),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isLast) FontWeight.SemiBold else FontWeight.Normal,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

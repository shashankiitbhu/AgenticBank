package com.copsiitbhu.agenticbank.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copsiitbhu.agenticbank.ui.components.AgenticColors
import com.copsiitbhu.agenticbank.viewmodel.AgentViewModel

@Composable
fun SuccessScreen(
    viewModel: AgentViewModel,
    onBackHome: () -> Unit
) {

    val formState by viewModel.transferFormState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "success")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AgenticColors.Background)
    ) {

        // Ambient success glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AgenticColors.AccentSuccess.copy(alpha = glowAlpha * 0.25f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.5f, size.height * 0.25f),
                            radius = 600f
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Success icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        AgenticColors.AccentSuccess.copy(alpha = 0.12f)
                    )
                    .border(
                        1.dp,
                        AgenticColors.AccentSuccess.copy(alpha = 0.4f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = AgenticColors.AccentSuccess,
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(700)) + slideInVertically(
                    initialOffsetY = { 40 }
                )
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Transfer Complete",
                        color = AgenticColors.TextPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "₹${formState.amount} sent to ${formState.recipient}",
                        color = AgenticColors.TextSecondary,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Transaction card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(AgenticColors.Surface)
                            .border(
                                1.dp,
                                AgenticColors.CardBorder,
                                RoundedCornerShape(24.dp)
                            )
                            .padding(24.dp)
                    ) {

                        Text(
                            text = "TRANSACTION SUMMARY",
                            color = AgenticColors.TextMuted,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        SummaryRow(
                            label = "Recipient",
                            value = formState.recipient
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SummaryRow(
                            label = "Amount",
                            value = "₹${formState.amount}"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SummaryRow(
                            label = "Frequency",
                            value = formState.frequency
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SummaryRow(
                            label = "Status",
                            value = "Completed"
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = onBackHome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            AgenticColors.Accent,
                                            AgenticColors.AccentSecondary
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Rounded.Home,
                                    contentDescription = null,
                                    tint = Color.White
                                )

                                Text(
                                    text = "Back Home",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color = AgenticColors.TextSecondary,
            fontSize = 14.sp
        )

        Text(
            text = value,
            color = AgenticColors.TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

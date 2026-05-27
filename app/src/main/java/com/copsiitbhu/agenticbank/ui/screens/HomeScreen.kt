package com.copsiitbhu.agenticbank.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.copsiitbhu.agenticbank.agent.*
import com.copsiitbhu.agenticbank.ui.components.*
import com.copsiitbhu.agenticbank.viewmodel.AgentViewModel
import com.copsiitbhu.agenticbank.viewmodel.HomeUiState
import com.copsiitbhu.agenticbank.viewmodel.Transaction

@Composable
fun HomeScreen(
    viewModel: AgentViewModel,
    onNavigateToTransfer: () -> Unit
) {
    val homeState by viewModel.homeUiState.collectAsState()
    val orchestrationState by viewModel.orchestrationState.collectAsState()
    val dynamicModules by viewModel.dynamicModules.collectAsState()

    HomeScreenContent(
        state = homeState,
        orchestrationState = orchestrationState,
        dynamicModules = dynamicModules,
        isAgentRunning = homeState.isAgentRunning,
        onPromptChange = viewModel::onPromptChanged,
        onPromptSubmit = viewModel::onPromptSubmitted
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    orchestrationState: com.copsiitbhu.agenticbank.agent.OrchestrationState,
    dynamicModules: List<com.copsiitbhu.agenticbank.ui.components.DynamicUiModule>,
    isAgentRunning: Boolean,
    onPromptChange: (String) -> Unit,
    onPromptSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AgenticColors.Background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 60.dp,
                bottom = 200.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item { HomeHeader(userName = state.userName) }

            // Balance card
            item { BalanceCard(balance = state.balance) }

            // Dynamic AI-Generated UI Modules
            item {
                DynamicUiRenderer(modules = dynamicModules)
            }

            // Execution Pipeline (only visible when agent is working)
            item {
                AnimatedVisibility(
                    visible = orchestrationState.reasoningLogs.isNotEmpty(),
                    enter = fadeIn() + expandVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ExecutionPipeline(currentStep = orchestrationState.currentPipelineStep)
                        StreamingReasoningPanel(logs = orchestrationState.reasoningLogs)
                    }
                }
            }

            // Quick action chips
            item { QuickActionRow() }

            // Recent Transactions
            item { SectionHeader(title = "Recent Transactions") }

            items(state.transactions, key = { it.id }) { tx ->
                TransactionRow(transaction = tx)
            }
        }

        // Floating AI input bar
        FloatingAgentBar(
            promptText = state.promptText,
            isRunning = isAgentRunning,
            onPromptChange = onPromptChange,
            onSubmit = {
                focusManager.clearFocus()
                onPromptSubmit()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        )
    }
}

@Composable
private fun AmbientGlow() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00D4FF).copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.1f),
                        radius = 400f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF7B61FF).copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.1f, size.height * 0.4f),
                        radius = 350f
                    )
                )
            }
    )
}

@Composable
private fun HomeHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good morning,",
                color = AgenticColors.TextSecondary,
                fontSize = 14.sp
            )
            Text(
                text = userName,
                color = AgenticColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(AgenticColors.Accent, AgenticColors.AccentSecondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun BalanceCard(balance: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFEDEEEE),
                        Color(0xFFEEEEF3)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        AgenticColors.Accent.copy(alpha = 0.4f),
                        AgenticColors.AccentSecondary.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        // Inner glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00D4FF).copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.8f, 0f),
                            radius = 200f
                        )
                    )
                }
        )
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "TOTAL BALANCE",
                    color = AgenticColors.TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AgenticColors.AccentSuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+2.4%",
                        color = AgenticColors.AccentSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            AnimatedBalance(targetBalance = balance)
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MiniStat(label = "Income", value = "₹85,000", color = AgenticColors.CreditGreen)
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(AgenticColors.CardBorder)
                )
                MiniStat(label = "Expenses", value = "₹2,290", color = AgenticColors.DebitRed)
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            color = AgenticColors.TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun QuickActionRow() {
    val actions = listOf("Send" to Icons.Rounded.Send, "Pay" to Icons.Rounded.Email, "History" to Icons.Rounded.DateRange, "More" to Icons.Rounded.MoreVert)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { (label, icon) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AgenticColors.Surface)
                    .border(1.dp, AgenticColors.CardBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = AgenticColors.Accent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = label,
                    color = AgenticColors.TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AgenticColors.Surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Emoji icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AgenticColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transaction.iconEmoji, fontSize = 20.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                color = AgenticColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.subtitle,
                color = AgenticColors.TextSecondary,
                fontSize = 12.sp
            )
        }

        Text(
            text = transaction.amount,
            color = if (transaction.isDebit) AgenticColors.DebitRed else AgenticColors.CreditGreen,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FloatingAgentBar(
    promptText: String,
    isRunning: Boolean,
    onPromptChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "barGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        // Glow halo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                AgenticColors.Accent.copy(alpha = glowAlpha * 0.15f)
                            )
                        )
                    )
                }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AgenticColors.Surface)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            AgenticColors.Accent.copy(alpha = glowAlpha),
                            AgenticColors.AccentSecondary.copy(alpha = glowAlpha * 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Agent avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(AgenticColors.Accent, AgenticColors.AccentSecondary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isRunning) {
                    PulsingDot(size = 8.dp, color = Color.White)
                } else {
                    Text("⚡", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.width(10.dp))

            TextField(
                value = promptText,
                onValueChange = onPromptChange,
                placeholder = {
                    Text(
                        text = if (isRunning) "Agent is working…" else "Ask agent to transfer funds…",
                        color = AgenticColors.TextMuted,
                        fontSize = 14.sp
                    )
                },
                enabled = !isRunning,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = AgenticColors.TextPrimary,
                    unfocusedTextColor = AgenticColors.TextPrimary,
                    cursorColor = AgenticColors.Accent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSubmit() }),
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            // Send button
            AnimatedContent(
                targetState = isRunning,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "sendBtn"
            ) { running ->
                if (running) {
                    CircularProgressIndicator(
                        color = AgenticColors.Accent,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AgenticColors.Accent, AgenticColors.AccentSecondary)
                                )
                            )
                            .clickable(enabled = promptText.isNotBlank()) { onSubmit() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFDFB)
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        state = com.copsiitbhu.agenticbank.viewmodel.HomeUiState(),
        orchestrationState = com.copsiitbhu.agenticbank.agent.OrchestrationState(),
        dynamicModules = emptyList(),
        isAgentRunning = false,
        onPromptChange = {},
        onPromptSubmit = {}
    )
}

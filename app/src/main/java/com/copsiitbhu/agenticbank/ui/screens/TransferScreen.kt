package com.copsiitbhu.agenticbank.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.copsiitbhu.agenticbank.agent.AgentExecutionState
import com.copsiitbhu.agenticbank.agent.AgentLog
import com.copsiitbhu.agenticbank.ui.components.*
import com.copsiitbhu.agenticbank.viewmodel.AgentViewModel
import com.copsiitbhu.agenticbank.viewmodel.TransferFormState


@Composable
fun TransferScreen(
    viewModel: AgentViewModel,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.transferFormState.collectAsState()
    val orchestrationState by viewModel.orchestrationState.collectAsState()

    TransferScreenContent(
        formState = formState,
        orchestrationState = orchestrationState,
        onRecipientChange = viewModel::onRecipientChanged,
        onAmountChange = viewModel::onAmountChanged,
        onFrequencyChange = viewModel::onFrequencyChanged,
        onTransfer = viewModel::onTransferClicked,
        onBack = onNavigateBack
    )
}

@Composable
private fun TransferScreenContent(
    formState: TransferFormState,
    orchestrationState: com.copsiitbhu.agenticbank.agent.OrchestrationState,
    onRecipientChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onFrequencyChange: (String) -> Unit,
    onTransfer: () -> Unit,
    onBack: () -> Unit
) {
    val isAgentControlled = orchestrationState.activeAgent != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AgenticColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(56.dp))

            // Header
            TransferHeader(onBack = onBack, isAgentControlled = isAgentControlled)

            Spacer(Modifier.height(32.dp))

            // Multi-Agent Orchestration Panel
            AnimatedVisibility(
                visible = orchestrationState.activeAgent != null || orchestrationState.reasoningLogs.isNotEmpty(),
                enter = fadeIn() + expandVertically()
            ) {
                AgentOrchestrationPanel(
                    agentStatuses = orchestrationState.agentStatuses,
                    activeAgent = orchestrationState.activeAgent,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // Transfer form card
            TransferFormCard(
                formState = formState,
                isAgentControlled = isAgentControlled,
                onRecipientChange = onRecipientChange,
                onAmountChange = onAmountChange,
                onFrequencyChange = onFrequencyChange
            )

            Spacer(Modifier.height(20.dp))

            // Reasoning log panel
            AnimatedVisibility(
                visible = orchestrationState.reasoningLogs.isNotEmpty(),
                enter = fadeIn() + expandVertically()
            ) {
                StreamingReasoningPanel(
                    logs = orchestrationState.reasoningLogs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
            }

            // Transfer button
            TransferButton(
                isSubmitting = formState.isSubmitting,
                isEnabled = formState.recipient.isNotBlank()
                        && formState.amount.isNotBlank()
                        && !formState.isSubmitting,
                onClick = onTransfer
            )

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun TransferHeader(onBack: () -> Unit, isAgentControlled: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AgenticColors.Surface)
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = AgenticColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        Column {
            Text(
                text = "Transfer Funds",
                color = AgenticColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            AnimatedContent(
                targetState = isAgentControlled,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "subtitle"
            ) { controlled ->
                Text(
                    text = if (controlled) "AI Agent is filling this form…" else "Secure UPI Transfer",
                    color = if (controlled) AgenticColors.Accent else AgenticColors.TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun AgentStatusBanner(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bannerAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AgenticColors.Accent.copy(alpha = 0.1f))
            .border(1.dp, AgenticColors.Accent.copy(alpha = alpha * 0.6f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PulsingDot(size = 8.dp, color = AgenticColors.Accent)
        Text(
            text = "AgenticBank AI is autonomously completing your transfer",
            color = AgenticColors.Accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TransferFormCard(
    formState: TransferFormState,
    isAgentControlled: Boolean,
    onRecipientChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onFrequencyChange: (String) -> Unit
) {
    var frequencyExpanded by remember { mutableStateOf(false) }
    val frequencies = listOf("One-time", "Daily", "Weekly", "Monthly")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AgenticColors.Surface)
            .border(1.dp, AgenticColors.CardBorder, RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Recipient field
        AgentTextField(
            label = "Recipient",
            value = formState.recipient,
            onValueChange = onRecipientChange,
            placeholder = "Enter name or UPI ID",
            leadingIcon = Icons.Rounded.Person,
            isHighlighted = formState.recipientHighlighted,
            isAgentTyping = formState.recipientHighlighted && isAgentControlled,
            keyboardType = KeyboardType.Text
        )

        // Amount field
        AgentTextField(
            label = "Amount",
            value = formState.amount,
            onValueChange = onAmountChange,
            placeholder = "₹0.00",
            leadingIcon = Icons.Rounded.Star,
            isHighlighted = formState.amountHighlighted,
            isAgentTyping = formState.amountHighlighted && isAgentControlled,
            keyboardType = KeyboardType.Number,
            prefix = if (formState.amount.isBlank()) null else "₹"
        )

        // Frequency dropdown
        Column {
            Text(
                text = "FREQUENCY",
                color = AgenticColors.TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .agentHighlight(active = formState.frequencyHighlighted)
                    .background(AgenticColors.SurfaceVariant)
                    .clickable { frequencyExpanded = true }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                            tint = AgenticColors.Accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        AnimatedContent(
                            targetState = formState.frequency,
                            transitionSpec = {
                                slideInVertically { -it } + fadeIn() togetherWith
                                        slideOutVertically { it } + fadeOut()
                            },
                            label = "frequency"
                        ) { freq ->
                            Text(
                                text = freq,
                                color = AgenticColors.TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = AgenticColors.TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = frequencyExpanded,
                    onDismissRequest = { frequencyExpanded = false },
                    modifier = Modifier.background(AgenticColors.SurfaceVariant)
                ) {
                    frequencies.forEach { freq ->
                        DropdownMenuItem(
                            text = {
                                Text(freq, color = AgenticColors.TextPrimary)
                            },
                            onClick = {
                                onFrequencyChange(freq)
                                frequencyExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = AgenticColors.TextPrimary
                            )
                        )
                    }
                }
            }
        }

        // Transfer summary (appears when form is filled)
        AnimatedVisibility(
            visible = formState.recipient.isNotBlank() && formState.amount.isNotBlank(),
            enter = fadeIn(tween(400)) + expandVertically(tween(400)),
        ) {
            TransferSummaryRow(
                recipient = formState.recipient,
                amount = formState.amount
            )
        }
    }
}

@Composable
private fun AgentTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isHighlighted: Boolean,
    isAgentTyping: Boolean,
    keyboardType: KeyboardType,
    prefix: String? = null
) {
    Column {
        Text(
            text = label.uppercase(),
            color = AgenticColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AgenticColors.SurfaceVariant)
                .agentHighlight(active = isHighlighted)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isHighlighted) AgenticColors.Accent else AgenticColors.TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))

                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(placeholder, color = AgenticColors.TextMuted, fontSize = 15.sp)
                    },
                    prefix = prefix?.let { { Text(it, color = AgenticColors.TextSecondary) } },
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
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.weight(1f)
                )

                // Agent typing indicator
                AnimatedVisibility(visible = isAgentTyping) {
                    PulsingDot(size = 6.dp, color = AgenticColors.Accent)
                }
            }
        }
    }
}

@Composable
private fun TransferSummaryRow(recipient: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AgenticColors.AccentSuccess.copy(alpha = 0.07f))
            .border(1.dp, AgenticColors.AccentSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Sending ₹$amount to $recipient",
            color = AgenticColors.AccentSuccess,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = AgenticColors.AccentSuccess,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun TransferButton(
    isSubmitting: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = AgenticColors.Surface
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isEnabled) {
                        Brush.linearGradient(
                            listOf(AgenticColors.Accent, AgenticColors.AccentSecondary)
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(AgenticColors.Accent, AgenticColors.Accent)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = isSubmitting,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                label = "btnContent"
            ) { submitting ->
                if (submitting) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Processing…",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = null,
                            tint = if (isEnabled) Color.White else AgenticColors.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Transfer Now",
                            color = if (isEnabled) Color.White else AgenticColors.TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFDFDFB)
@Composable
fun TransferScreenPreview() {
    TransferScreenContent(
        formState = TransferFormState(recipient = "Aman", amount = "500"),
        orchestrationState = com.copsiitbhu.agenticbank.agent.OrchestrationState(),
        onRecipientChange = {},
        onAmountChange = {},
        onFrequencyChange = {},
        onTransfer = {},
        onBack = {}
    )
}

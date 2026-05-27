package com.copsiitbhu.agenticbank.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single atomic action that the AI agent can perform.
 * Actions are parsed from the AI response and executed sequentially.
 */
@Serializable
data class AgentAction(
    @SerialName("action") val action: ActionType,
    @SerialName("screen") val screen: ScreenTarget,
    @SerialName("field") val field: String? = null,
    @SerialName("value") val value: String? = null,
    @SerialName("delay_ms") val delayMs: Long = 600L
)

@Serializable
enum class ActionType {
    @SerialName("navigate") NAVIGATE,
    @SerialName("fill") FILL,
    @SerialName("submit") SUBMIT,
    @SerialName("highlight") HIGHLIGHT,
    @SerialName("show_toast") SHOW_TOAST
}

@Serializable
enum class ScreenTarget {
    @SerialName("home") HOME,
    @SerialName("transfer") TRANSFER,
    @SerialName("success") SUCCESS,
    @SerialName("none") NONE
}

/**
 * Represents a log entry produced during agent execution.
 */
data class AgentLog(
    val timestamp: Long = System.currentTimeMillis(),
    val message: String,
    val type: LogType = LogType.INFO
)

enum class LogType { INFO, ACTION, SUCCESS, ERROR }

/**
 * Sealed hierarchy for the overall agent execution state.
 */
sealed class AgentExecutionState {
    object Idle : AgentExecutionState()
    data class Running(val stepIndex: Int, val totalSteps: Int) : AgentExecutionState()
    data class Completed(val logs: List<AgentLog>) : AgentExecutionState()
    data class Failed(val reason: String) : AgentExecutionState()
}

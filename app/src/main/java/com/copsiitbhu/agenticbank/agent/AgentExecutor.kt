package com.copsiitbhu.agenticbank.agent

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AgentExecutor is responsible for orchestrating the sequential execution
 * of AI-generated actions with cinematic delays to simulate an AI agent
 * autonomously operating the banking UI.
 */
class AgentExecutor {

    private val _executionState = MutableStateFlow<AgentExecutionState>(AgentExecutionState.Idle)
    val executionState: StateFlow<AgentExecutionState> = _executionState.asStateFlow()

    private val _currentStep = MutableStateFlow(-1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _executionLogs = MutableStateFlow<List<AgentLog>>(emptyList())
    val executionLogs: StateFlow<List<AgentLog>> = _executionLogs.asStateFlow()

    private val _currentAction = MutableStateFlow<AgentAction?>(null)
    val currentAction: StateFlow<AgentAction?> = _currentAction.asStateFlow()

    /**
     * Executes a list of [AgentAction]s sequentially with delays between each step.
     * Each action triggers the appropriate [onAction] callback, allowing the ViewModel
     * to respond (navigate, fill fields, submit forms, etc.).
     */
    suspend fun execute(
        actions: List<AgentAction>,
        onAction: suspend (AgentAction) -> Unit
    ) {
        _executionLogs.value = emptyList()
        _currentStep.value = 0
        _executionState.value = AgentExecutionState.Running(0, actions.size)

        appendLog(AgentLog(message = "⚡ Agent initialized. Preparing ${actions.size} actions…", type = LogType.INFO))
        delay(400)

        actions.forEachIndexed { index, action ->
            _currentStep.value = index
            _currentAction.value = action
            _executionState.value = AgentExecutionState.Running(index, actions.size)

            val logMessage = buildLogMessage(action)
            appendLog(AgentLog(message = logMessage, type = LogType.ACTION))

            try {
                onAction(action)
                delay(action.delayMs)
                appendLog(AgentLog(message = "✓ Step ${index + 1} completed", type = LogType.SUCCESS))
            } catch (e: Exception) {
                val errorMsg = "✗ Step ${index + 1} failed: ${e.message}"
                appendLog(AgentLog(message = errorMsg, type = LogType.ERROR))
                _executionState.value = AgentExecutionState.Failed(errorMsg)
                _currentAction.value = null
                return
            }

            // Inter-step cinematic pause
            if (index < actions.size - 1) {
                delay(300)
            }
        }

        _currentAction.value = null
        _currentStep.value = -1
        appendLog(AgentLog(message = "✓ All actions executed successfully.", type = LogType.SUCCESS))
        _executionState.value = AgentExecutionState.Completed(_executionLogs.value)
    }

    fun reset() {
        _executionState.value = AgentExecutionState.Idle
        _currentStep.value = -1
        _executionLogs.value = emptyList()
        _currentAction.value = null
    }

    fun appendLog(log: AgentLog) {
        _executionLogs.value = _executionLogs.value + log
    }

    fun setExecutionState(state: AgentExecutionState) {
        _executionState.value = state
    }

    private fun buildLogMessage(action: AgentAction): String = when (action.action) {
        ActionType.NAVIGATE -> "→ Navigating to [${action.screen.name.lowercase()}] screen"
        ActionType.FILL -> "✎ Filling [${action.field ?: "unknown"}] with \"${action.value ?: ""}\""
        ActionType.SUBMIT -> "▶ Submitting form on [${action.screen.name.lowercase()}]"
        ActionType.HIGHLIGHT -> "◈ Highlighting [${action.field ?: "element"}]"
        ActionType.SHOW_TOAST -> "◎ Showing message: \"${action.value ?: ""}\""
    }
}

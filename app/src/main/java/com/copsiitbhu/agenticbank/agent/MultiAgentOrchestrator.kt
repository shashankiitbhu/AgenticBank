package com.copsiitbhu.agenticbank.agent

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OrchestrationState(
    val activeAgent: AgentType? = null,
    val agentStatuses: Map<AgentType, AgentStatus> = AgentType.values().associateWith { AgentStatus.IDLE },
    val reasoningLogs: List<String> = emptyList(),
    val currentPipelineStep: Int = 0
)

class MultiAgentOrchestrator {
    private val _state = MutableStateFlow(OrchestrationState())
    val state: StateFlow<OrchestrationState> = _state.asStateFlow()

    suspend fun startOrchestration() {
        _state.update { 
            it.copy(
                activeAgent = null,
                agentStatuses = AgentType.values().associateWith { AgentStatus.IDLE },
                reasoningLogs = emptyList(),
                currentPipelineStep = 0
            )
        }
    }

    suspend fun transitionTo(agent: AgentType, reasoning: String? = null) {
        // Mark previous agent as success if exists
        _state.update { current ->
            val updatedStatuses = current.agentStatuses.toMutableMap()
            current.activeAgent?.let { updatedStatuses[it] = AgentStatus.SUCCESS }
            updatedStatuses[agent] = AgentStatus.ACTIVE
            
            val newLogs = if (reasoning != null) current.reasoningLogs + reasoning else current.reasoningLogs
            
            current.copy(
                activeAgent = agent,
                agentStatuses = updatedStatuses,
                reasoningLogs = newLogs,
                currentPipelineStep = agent.ordinal
            )
        }
        delay(600) // Cinematic handoff delay
    }

    suspend fun addReasoning(text: String) {
        _state.update { it.copy(reasoningLogs = it.reasoningLogs + text) }
        delay(400)
    }

    suspend fun markComplete() {
        _state.update { current ->
            val updatedStatuses = current.agentStatuses.toMutableMap()
            current.activeAgent?.let { updatedStatuses[it] = AgentStatus.SUCCESS }
            current.copy(
                activeAgent = null,
                agentStatuses = updatedStatuses,
                currentPipelineStep = 4 // Beyond the last stage
            )
        }
        addReasoning("Orchestration complete. Pipeline released.")
    }

    suspend fun markError(message: String) {
        _state.update { current ->
            val updatedStatuses = current.agentStatuses.toMutableMap()
            current.activeAgent?.let { updatedStatuses[it] = AgentStatus.ERROR }
            current.copy(
                activeAgent = null,
                agentStatuses = updatedStatuses,
                reasoningLogs = current.reasoningLogs + "✗ Error: $message"
            )
        }
    }

    fun reset() {
        _state.value = OrchestrationState()
    }
}

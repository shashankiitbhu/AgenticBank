package com.copsiitbhu.agenticbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copsiitbhu.agenticbank.agent.*
import com.copsiitbhu.agenticbank.ui.components.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

// Agent Development Kit (ADK) imports
import com.google.adk.kt.agents.LlmAgent
import com.google.adk.kt.agents.Instruction
import com.google.adk.kt.models.Gemini
import com.google.adk.kt.annotations.Tool
import com.google.adk.kt.annotations.Param
import com.google.adk.kt.runners.InMemoryRunner
import com.google.adk.kt.types.Content
import com.google.adk.kt.types.Role

// ---------------------------------------------------------------------------
// UI State models
// ---------------------------------------------------------------------------

data class TransferFormState(
    val recipient: String = "",
    val amount: String = "",
    val frequency: String = "One-time",
    val recipientHighlighted: Boolean = false,
    val amountHighlighted: Boolean = false,
    val frequencyHighlighted: Boolean = false,
    val isSubmitting: Boolean = false
)

data class HomeUiState(
    val balance: String = "₹1,24,580.00",
    val userName: String = "Shashank",
    val promptText: String = "",
    val isAgentRunning: Boolean = false,
    val transactions: List<Transaction> = sampleTransactions()
)

data class Transaction(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val isDebit: Boolean,
    val iconEmoji: String
)

data class SuccessState(
    val recipientName: String = "",
    val amount: String = ""
)

private fun sampleTransactions() = listOf(
    Transaction("1", "Swiggy", "Food & Drinks", "−₹342", true, "🍔"),
    Transaction("2", "Salary Credit", "HDFC Bank", "+₹85,000", false, "💼"),
    Transaction("3", "Netflix", "Subscription", "−₹649", true, "🎬"),
    Transaction("4", "Aman Gupta", "UPI Transfer", "+₹2,000", false, "👤"),
    Transaction("5", "Amazon", "Shopping", "−₹1,299", true, "📦")
)


// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class AgentViewModel : ViewModel() {

    private val agentExecutor = AgentExecutor()
    private val multiAgentOrchestrator = MultiAgentOrchestrator()

    // --- Orchestration state
    val orchestrationState = multiAgentOrchestrator.state

    // --- Dynamic UI state
    private val _dynamicModules = MutableStateFlow<List<DynamicUiModule>>(emptyList())
    val dynamicModules: StateFlow<List<DynamicUiModule>> = _dynamicModules.asStateFlow()

    // --- ADK Agent Configuration
    private val geminiModel = Gemini(
        name = "gemini-1.5-flash",
        apiKey = "YOUR_API_KEY_HERE" // Replace with actual API key
    )

    private val bankAgent = LlmAgent(
        name = "BankingAgent",
        model = geminiModel,
        instruction = Instruction(
            "You are an autonomous banking assistant. Your goal is to help the user perform transactions. " +
            "You can navigate screens, fill forms, and submit them. Always navigate to the transfer screen " +
            "before trying to fill it. Highlight fields before filling them for better UI feedback."
        ),
        tools = this.generatedTools()
    )

    // --- HomeScreen state
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    // --- TransferScreen state
    private val _transferFormState = MutableStateFlow(TransferFormState())
    val transferFormState: StateFlow<TransferFormState> = _transferFormState.asStateFlow()

    // --- SuccessScreen state
    private val _successState = MutableStateFlow(SuccessState())
    val successState: StateFlow<SuccessState> = _successState.asStateFlow()

    // --- Agent execution state (forwarded from executor)
    val executionState: StateFlow<AgentExecutionState> = agentExecutor.executionState
    val executionLogs: StateFlow<List<AgentLog>> = agentExecutor.executionLogs
    val currentStep: StateFlow<Int> = agentExecutor.currentStep
    val currentAction: StateFlow<AgentAction?> = agentExecutor.currentAction

    // Navigation command — consumed by NavGraph
    private val _navigationCommand = MutableStateFlow<String?>(null)
    val navigationCommand: StateFlow<String?> = _navigationCommand.asStateFlow()

    // ---------------------------------------------------------------------------
    // Home prompt input
    // ---------------------------------------------------------------------------

    fun onPromptChanged(text: String) {
        _homeUiState.update { it.copy(promptText = text) }
    }

    fun onPromptSubmitted() {
        val prompt = _homeUiState.value.promptText.trim()
        if (prompt.isBlank()) return
        generateActions(prompt)
    }

    // ---------------------------------------------------------------------------
    // Fake Gemini integration
    // Simulates a network call + LLM parsing. In production, replace with
    // actual Gemini / Vertex AI SDK call.
    // ---------------------------------------------------------------------------

    private fun generateActions(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _homeUiState.update { it.copy(isAgentRunning = true) }
            agentExecutor.reset()
            multiAgentOrchestrator.reset()
            _dynamicModules.value = emptyList()
            
            // 1. Planner Agent
            multiAgentOrchestrator.transitionTo(AgentType.PLANNER, "Analyzing user intent: '$prompt'...")
            delay(1200)

            // 2. UI Generation Agent - Dynamic Layout Composition
            multiAgentOrchestrator.transitionTo(AgentType.UI_GENERATION, "Decomposing intent into dynamic UI modules...")
            delay(800)
            
            val p = prompt.lowercase()
            when {
                p.contains("split") -> {
                    multiAgentOrchestrator.addReasoning("Detected split payment intent. Generating settlement workflow...")
                    val splitModule = DynamicUiModule.SplitPayment(
                        participants = listOf(
                            Participant("Arjun", "👤"),
                            Participant("Sneha", "👩"),
                            Participant("Aman", "👨"),
                            Participant("You", "📱")
                        ),
                        totalAmount = "2,000",
                        perPersonAmount = "500"
                    )
                    _dynamicModules.update { it + splitModule }
                    delay(1000)
                    multiAgentOrchestrator.addReasoning("Injecting participant list and settlement modules...")
                    _dynamicModules.update { it + DynamicUiModule.ParticipantList(splitModule.participants) }
                }
                p.contains("monthly") || p.contains("rent") -> {
                    multiAgentOrchestrator.addReasoning("Detected recurring payment intent. Building monthly workflow...")
                    _dynamicModules.update { it + DynamicUiModule.RecurringPayment("45,000", "Monthly", "1st June") }
                    delay(1000)
                    multiAgentOrchestrator.addReasoning("Generating reminder timeline and approval steps...")
                    _dynamicModules.update { it + DynamicUiModule.ReminderTimeline(listOf(
                        TimelineEvent("Invoice Generated", "25th May", true),
                        TimelineEvent("Awaiting Funds", "1st June", false),
                        TimelineEvent("Auto-pay Execution", "2nd June", false)
                    )) }
                }
                p.contains("track") || p.contains("spend") || p.contains("insight") -> {
                    multiAgentOrchestrator.addReasoning("Detected insight request. Generating financial analytics...")
                    _dynamicModules.update { it + DynamicUiModule.ExpenseInsight("Food & Drinks", "24%", "↑ 12% vs last month") }
                    delay(1200)
                    multiAgentOrchestrator.addReasoning("Identifying anomalies... Generating security banners...")
                    _dynamicModules.update { it + DynamicUiModule.FraudBanner("Large transaction at 'Unknown Merchant' detected.", "MEDIUM") }
                }
                else -> {
                    multiAgentOrchestrator.addReasoning("Standard transaction detected. Generating smart recommendations...")
                    _dynamicModules.update { it + DynamicUiModule.SmartRecommendation(
                        "Quick Pay to Aman",
                        "You usually send money to Aman on Fridays.",
                        "Transfer ₹500 Now"
                    ) }
                }
            }
            delay(1000)

            try {
                // Clear previous form state
                _transferFormState.value = TransferFormState()

                // 3. Navigation Agent
                multiAgentOrchestrator.transitionTo(AgentType.NAVIGATION, "Plan finalized. Transitioning to execution...")
                delay(500)
                
                // If it's a standard transfer (not just UI generation), proceed to ADK
                if (!p.contains("insight") && !p.contains("track")) {
                    val runner = InMemoryRunner(bankAgent, appName = "AgenticBank")
                    val userMessage = Content.fromText(Role.USER, prompt)
                    
                    runner.runAsync(
                        userId = "bank-user",
                        sessionId = "session-1",
                        newMessage = userMessage
                    ).collect { /* streaming events */ }
                }
                
                multiAgentOrchestrator.markComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                multiAgentOrchestrator.markError("${e.javaClass.simpleName}: ${e.message}")
            } finally {
                _homeUiState.update { it.copy(isAgentRunning = false, promptText = "") }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // ADK Tools
    // ---------------------------------------------------------------------------

    @Tool(description = "Navigate the user to a specific screen: 'home', 'transfer', or 'success'")
    suspend fun navigateTo(
        @Param(description = "The target screen name") screenName: String
    ) {
        multiAgentOrchestrator.transitionTo(AgentType.NAVIGATION, "Navigating to $screenName screen...")
        val target = when (screenName.lowercase()) {
            "home" -> ScreenTarget.HOME
            "transfer" -> ScreenTarget.TRANSFER
            "success" -> ScreenTarget.SUCCESS
            else -> ScreenTarget.NONE
        }
        handleNavigate(target)
        delay(800)
    }

    @Tool(description = "Fill a specific field in the transfer form")
    suspend fun fillField(
        @Param(description = "The field to fill: 'recipient', 'amount', or 'frequency'") field: String,
        @Param(description = "The value to enter in the field") value: String
    ) {
        // Only transition to FORM agent once if not already there
        if (multiAgentOrchestrator.state.value.activeAgent != AgentType.FORM) {
            multiAgentOrchestrator.transitionTo(AgentType.FORM, "Switching to FormAgent for data entry...")
        }
        
        multiAgentOrchestrator.addReasoning("Entering $field: \"$value\"...")
        handleHighlight(field) // Highlight for feedback
        handleFill(field, value)
        delay(600)
        handleHighlight(null) // Clear highlight
    }

    @Tool(description = "Submit the current transaction/form")
    suspend fun submitTransaction() {
        multiAgentOrchestrator.transitionTo(AgentType.VALIDATION, "Running security checks...")
        delay(1200)
        multiAgentOrchestrator.addReasoning("Validating transfer details with core banking...")
        delay(1000)
        multiAgentOrchestrator.addReasoning("Transaction approved by ValidationAgent.")
        delay(800)
        handleSubmit()
    }

    @Tool(description = "Highlight a UI element to guide the user")
    suspend fun highlightElement(
        @Param(description = "The element name to highlight (e.g. 'recipient', 'amount')") field: String
    ) {
        handleHighlight(field)
        delay(400)
    }

    private fun handleNavigate(screen: ScreenTarget) {
        _navigationCommand.value = when (screen) {
            ScreenTarget.HOME -> "home"
            ScreenTarget.TRANSFER -> "transfer"
            ScreenTarget.SUCCESS -> "success"
            ScreenTarget.NONE -> null
        }
    }

    private fun handleFill(field: String?, value: String?) {
        if (field == null || value == null) return
        _transferFormState.update { current ->
            when (field.lowercase()) {
                "recipient" -> current.copy(
                    recipient = value,
                    recipientHighlighted = false
                )
                "amount" -> current.copy(
                    amount = value,
                    amountHighlighted = false
                )
                "frequency" -> current.copy(
                    frequency = value,
                    frequencyHighlighted = false
                )
                else -> current
            }
        }
    }

    private fun handleHighlight(field: String?) {
        if (field == null) return
        _transferFormState.update { current ->
            when (field.lowercase()) {
                "recipient" -> current.copy(
                    recipientHighlighted = true,
                    amountHighlighted = false,
                    frequencyHighlighted = false
                )
                "amount" -> current.copy(
                    recipientHighlighted = false,
                    amountHighlighted = true,
                    frequencyHighlighted = false
                )
                "frequency" -> current.copy(
                    recipientHighlighted = false,
                    amountHighlighted = false,
                    frequencyHighlighted = true
                )
                else -> current
            }
        }
    }

    private suspend fun handleSubmit() {
        _transferFormState.update { it.copy(isSubmitting = true) }
        delay(800)
        val form = _transferFormState.value
        _successState.value = SuccessState(
            recipientName = form.recipient,
            amount = form.amount
        )
        
        _homeUiState.update { current ->
            val cleanAmount = form.amount.replace(Regex("[^0-9]"), "")
            val amountValue = cleanAmount.toLongOrNull() ?: 0L
            
            // Update balance (simple subtraction for demo)
            val currentBalance = current.balance.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 12458000L
            val newBalanceValue = currentBalance - (amountValue * 100) // Assuming balance is in paise-like format
            val formattedBalance = "₹" + String.format(Locale.getDefault(), "%,.2f", newBalanceValue / 100.0)

            // Create new transaction object
            val newTransaction = Transaction(
                id = System.currentTimeMillis().toString(),
                title = form.recipient,
                subtitle = "UPI Transfer",
                amount = "−₹${form.amount}",
                isDebit = true,
                iconEmoji = "👤"
            )

            current.copy(
                balance = formattedBalance,
                transactions = listOf(newTransaction) + current.transactions
            )
        }
        // ----------------------------------------------------

        _transferFormState.update { it.copy(isSubmitting = false) }
        handleNavigate(ScreenTarget.SUCCESS)
    }

    // ---------------------------------------------------------------------------
    // Manual form editing (when user types directly)
    // ---------------------------------------------------------------------------

    fun onRecipientChanged(value: String) =
        _transferFormState.update { it.copy(recipient = value) }

    fun onAmountChanged(value: String) =
        _transferFormState.update { it.copy(amount = value) }

    fun onFrequencyChanged(value: String) =
        _transferFormState.update { it.copy(frequency = value) }

    fun onTransferClicked() {
        viewModelScope.launch { handleSubmit() }
    }

    // ---------------------------------------------------------------------------
    // Navigation consumption
    // ---------------------------------------------------------------------------

    fun consumeNavigationCommand() {
        _navigationCommand.value = null
    }

    fun onResetToHome() {
        _transferFormState.value = TransferFormState()
        _successState.value = SuccessState()
        agentExecutor.reset()
        _homeUiState.update { it.copy(isAgentRunning = false) }
        _navigationCommand.value = "home"
    }
}

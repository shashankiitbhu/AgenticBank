package com.copsiitbhu.agenticbank.ui.components

import java.util.UUID

sealed class DynamicUiModule(val id: String = UUID.randomUUID().toString()) {
    
    data class SplitPayment(
        val participants: List<Participant>,
        val totalAmount: String,
        val perPersonAmount: String
    ) : DynamicUiModule()

    data class RecurringPayment(
        val amount: String,
        val frequency: String,
        val nextDate: String
    ) : DynamicUiModule()

    data class ReminderTimeline(
        val events: List<TimelineEvent>
    ) : DynamicUiModule()

    data class ApprovalWorkflow(
        val steps: List<WorkflowStep>
    ) : DynamicUiModule()

    data class ExpenseInsight(
        val category: String,
        val percentage: String,
        val trend: String
    ) : DynamicUiModule()

    data class FraudBanner(
        val message: String,
        val severity: String
    ) : DynamicUiModule()

    data class ParticipantList(
        val people: List<Participant>
    ) : DynamicUiModule()

    data class SmartRecommendation(
        val title: String,
        val description: String,
        val actionText: String
    ) : DynamicUiModule()
}

data class Participant(val name: String, val avatar: String, val amount: String? = null)
data class TimelineEvent(val title: String, val date: String, val isCompleted: Boolean)
data class WorkflowStep(val label: String, val status: String)

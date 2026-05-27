## AgenticBank - Full Autonomous System

AgenticBank Autonomous multi-agent banking system. Powered by Gemini and Google ADK. Built with Jetpack Compose.
Features:
• Intent-driven UI generation: interface morphs based on what you say.
• 5 specialized agents: Planner, UI Gen, Navigation, Form, and Validation.
• Real-time orchestration: agents collaborate and hand off tasks.
• Live reasoning panel: visible execution logs and AI thought process.
• Dynamic modules: split-pay, recurring timelines, and spending insights generated on the fly.
• Automated workflows: navigates, fills fields, and submits transactions autonomously.

**Setup**:
• Requires Gemini API key in AgentViewModel.kt
• Uses Kotlin Symbol Processing (KSP) for ADK tool generation.

### How does it work
You simply tell the app what is in your mind. Like "split this 2000 bill between Arjun and me monthly." or "Send money to Aman" Then you just sit and watch. The app is not waiting for you to type; it is building the screens on its own!

Note : Rest of App contains dummy data, this is only built to demo the ADK's capability to build agents.

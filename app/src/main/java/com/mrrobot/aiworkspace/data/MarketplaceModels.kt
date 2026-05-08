package com.mrrobot.aiworkspace.data

enum class MarketplaceItemType {
    Model,
    Agent,
    Tool,
    Workflow,
    Integration
}

data class MarketplaceItem(
    val id: String,
    val title: String,
    val provider: String,
    val type: MarketplaceItemType,
    val description: String,
    val tags: List<String>,
    val enabled: Boolean = false
)

object MarketplaceCatalog {
    fun items(): List<MarketplaceItem> {
        return listOf(
            MarketplaceItem(
                id = "openrouter_models",
                title = "OpenRouter Model Gateway",
                provider = "OpenRouter",
                type = MarketplaceItemType.Integration,
                description = "Use multiple AI models through one API key and route requests from the chat workspace.",
                tags = listOf("AI", "Models", "API"),
                enabled = true
            ),
            MarketplaceItem(
                id = "android_architect_agent",
                title = "Android Architect Agent",
                provider = "Mr. Robot",
                type = MarketplaceItemType.Agent,
                description = "Generates Android project structures, Compose screens, ViewModels, repositories, and Gradle fixes.",
                tags = listOf("Kotlin", "Compose", "Gradle"),
                enabled = true
            ),
            MarketplaceItem(
                id = "debug_specialist_agent",
                title = "Debugging Specialist Agent",
                provider = "Mr. Robot",
                type = MarketplaceItemType.Agent,
                description = "Analyzes build logs, Kotlin errors, dependency conflicts, and GitHub Actions failures.",
                tags = listOf("Debug", "CI", "Logs"),
                enabled = true
            ),
            MarketplaceItem(
                id = "apk_build_workflow",
                title = "APK Build Workflow",
                provider = "GitHub Actions",
                type = MarketplaceItemType.Workflow,
                description = "Builds Android debug APKs, uploads artifacts, and stores build logs.",
                tags = listOf("APK", "CI/CD", "Artifacts"),
                enabled = true
            ),
            MarketplaceItem(
                id = "markdown_prompt_builder",
                title = "Markdown Prompt Builder",
                provider = "Mr. Robot",
                type = MarketplaceItemType.Tool,
                description = "Creates structured prompts for agents, workflows, Android builds, and UI upgrades.",
                tags = listOf("Prompt", "Markdown", "Agents")
            ),
            MarketplaceItem(
                id = "stitch_importer",
                title = "Google Stitch Design Importer",
                provider = "Google Stitch",
                type = MarketplaceItemType.Tool,
                description = "Tracks Stitch design ZIP exports and maps generated screens into Android Compose screens.",
                tags = listOf("Design", "Compose", "UI")
            ),
            MarketplaceItem(
                id = "gemini_flash",
                title = "Gemini 2.0 Flash",
                provider = "Google",
                type = MarketplaceItemType.Model,
                description = "Fast model for mobile AI assistant workflows and UI generation.",
                tags = listOf("Fast", "Google", "Model")
            ),
            MarketplaceItem(
                id = "gpt4o_mini",
                title = "GPT-4o Mini",
                provider = "OpenAI",
                type = MarketplaceItemType.Model,
                description = "Balanced general model for chat, coding help, and productivity workflows.",
                tags = listOf("OpenAI", "Chat", "Coding"),
                enabled = true
            ),
            MarketplaceItem(
                id = "claude_sonnet",
                title = "Claude 3.5 Sonnet",
                provider = "Anthropic",
                type = MarketplaceItemType.Model,
                description = "Strong writing, reasoning, and large task planning model.",
                tags = listOf("Reasoning", "Writing", "Planning")
            )
        )
    }
}

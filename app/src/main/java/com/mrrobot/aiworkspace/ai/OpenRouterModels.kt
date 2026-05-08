package com.mrrobot.aiworkspace.ai

data class OpenRouterModel(
    val id: String,
    val title: String
)

object OpenRouterModels {

    val models = listOf(

        OpenRouterModel(
            "openai/gpt-4o-mini",
            "GPT-4o Mini"
        ),

        OpenRouterModel(
            "anthropic/claude-3.5-sonnet",
            "Claude Sonnet"
        ),

        OpenRouterModel(
            "google/gemini-flash-1.5",
            "Gemini Flash"
        ),

        OpenRouterModel(
            "meta-llama/llama-3.1-70b-instruct",
            "Llama 3.1 70B"
        )
    )
}

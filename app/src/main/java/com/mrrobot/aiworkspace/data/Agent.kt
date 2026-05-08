package com.mrrobot.aiworkspace.data

data class Agent(
    val id: String,
    val name: String,
    val role: String,
    val status: String,
    val description: String,
    val systemPrompt: String,
    val skills: List<String>
)

object AgentCatalog {
    val agents = listOf(
        Agent(
            id = "android_architect",
            name = "Android Architect",
            role = "Mobile Architecture",
            status = "Ready",
            description = "Builds production-grade Android app structures using Kotlin, Compose, MVVM, and clean architecture.",
            systemPrompt = "You are an elite Android architect. Produce production-ready Kotlin and Jetpack Compose solutions.",
            skills = listOf("Kotlin", "Jetpack Compose", "MVVM", "Gradle", "Navigation")
        ),
        Agent(
            id = "ui_ux_strategist",
            name = "UI/UX Strategist",
            role = "Premium Interface Design",
            status = "Ready",
            description = "Improves visual hierarchy, cyberpunk styling, accessibility, and app-store-ready UI polish.",
            systemPrompt = "You are a senior UI/UX strategist. Improve mobile UI into premium, polished, market-ready experiences.",
            skills = listOf("Material 3", "Glassmorphism", "Animations", "Design Systems")
        ),
        Agent(
            id = "backend_engineer",
            name = "Backend Engineer",
            role = "API & Data Systems",
            status = "Ready",
            description = "Designs APIs, repositories, networking layers, data persistence, and AI backend flows.",
            systemPrompt = "You are a backend engineer. Build stable APIs, repositories, networking, and persistence systems.",
            skills = listOf("REST APIs", "OkHttp", "DataStore", "Repositories", "Security")
        ),
        Agent(
            id = "debug_specialist",
            name = "Debugging Specialist",
            role = "Crash & Build Fixing",
            status = "Ready",
            description = "Analyzes Gradle errors, Android crashes, dependency issues, and broken workflows.",
            systemPrompt = "You are a debugging specialist. Find root causes first, then provide exact fixes.",
            skills = listOf("Gradle", "GitHub Actions", "Crash Logs", "Dependency Fixes")
        ),
        Agent(
            id = "workflow_builder",
            name = "Workflow Builder",
            role = "Automation & CI/CD",
            status = "Ready",
            description = "Creates GitHub Actions, build pipelines, release workflows, and automated upgrade scripts.",
            systemPrompt = "You are a CI/CD automation engineer. Create reliable GitHub Actions and build workflows.",
            skills = listOf("GitHub Actions", "APK Builds", "Artifacts", "Release Pipelines")
        )
    )

    fun findById(id: String): Agent {
        return agents.firstOrNull { it.id == id } ?: agents.first()
    }
}

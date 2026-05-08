package com.mrrobot.aiworkspace.data

data class WorkflowStep(
    val id: Long,
    val title: String,
    val description: String,
    val agent: String,
    val status: WorkflowStatus = WorkflowStatus.Pending
)

enum class WorkflowStatus {
    Pending,
    Running,
    Completed,
    Failed
}

object WorkflowTemplates {
    fun androidAppBuild(): List<WorkflowStep> {
        return listOf(
            WorkflowStep(
                id = 1,
                title = "Analyze App Request",
                description = "Read requirements, detect screens, features, architecture, and build constraints.",
                agent = "Android Architect"
            ),
            WorkflowStep(
                id = 2,
                title = "Design UI System",
                description = "Create premium Material 3 cyberpunk UI components and navigation.",
                agent = "UI/UX Strategist"
            ),
            WorkflowStep(
                id = 3,
                title = "Generate Android Code",
                description = "Create Compose screens, ViewModels, repositories, and Gradle configuration.",
                agent = "Android Architect"
            ),
            WorkflowStep(
                id = 4,
                title = "Add Backend/API Layer",
                description = "Wire OpenRouter, DataStore, network requests, and persistence.",
                agent = "Backend Engineer"
            ),
            WorkflowStep(
                id = 5,
                title = "Debug Build",
                description = "Fix Gradle, Kotlin, dependency, and runtime errors.",
                agent = "Debugging Specialist"
            ),
            WorkflowStep(
                id = 6,
                title = "Create APK Workflow",
                description = "Generate GitHub Actions workflow to build and upload APK artifacts.",
                agent = "Workflow Builder"
            )
        )
    }

    fun aiWorkspaceUpgrade(): List<WorkflowStep> {
        return listOf(
            WorkflowStep(
                id = 1,
                title = "Inspect Existing App",
                description = "Review current package structure, screens, and missing capabilities.",
                agent = "Debugging Specialist"
            ),
            WorkflowStep(
                id = 2,
                title = "Upgrade Chat System",
                description = "Add streaming, copy, retry, regenerate, and model selection.",
                agent = "Backend Engineer"
            ),
            WorkflowStep(
                id = 3,
                title = "Upgrade Agents",
                description = "Add functional agents, prompt generation, and task routing.",
                agent = "Android Architect"
            ),
            WorkflowStep(
                id = 4,
                title = "Upgrade UI Polish",
                description = "Add animations, glass cards, glow, icons, and responsive polish.",
                agent = "UI/UX Strategist"
            )
        )
    }
}

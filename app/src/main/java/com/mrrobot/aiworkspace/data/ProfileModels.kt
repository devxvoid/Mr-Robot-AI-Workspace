package com.mrrobot.aiworkspace.data

data class WorkspaceProfile(
    val displayName: String,
    val title: String,
    val handle: String,
    val principle: String,
    val github: String,
    val website: String,
    val xProfile: String,
    val status: String
)

data class WorkspaceStat(
    val label: String,
    val value: String,
    val description: String
)

data class WorkspaceCapability(
    val name: String,
    val description: String,
    val enabled: Boolean
)

object ProfileDefaults {
    fun profile(): WorkspaceProfile {
        return WorkspaceProfile(
            displayName = "Mr. Robot",
            title = "AI Workspace Operator",
            handle = "devxvoid",
            principle = "When we lose our principles, we invite chaos.",
            github = "github.com/devxvoid",
            website = "aakashpanta.blog",
            xProfile = "x.com/i_am_apc",
            status = "Workspace Active"
        )
    }

    fun stats(): List<WorkspaceStat> {
        return listOf(
            WorkspaceStat(
                label = "Screens",
                value = "10",
                description = "Core Android MVP screens"
            ),
            WorkspaceStat(
                label = "Agents",
                value = "5",
                description = "Specialized execution agents"
            ),
            WorkspaceStat(
                label = "Workflows",
                value = "2",
                description = "Reusable pipeline templates"
            ),
            WorkspaceStat(
                label = "AI",
                value = "OpenRouter",
                description = "Multi-model provider gateway"
            )
        )
    }

    fun capabilities(): List<WorkspaceCapability> {
        return listOf(
            WorkspaceCapability(
                name = "OpenRouter Chat",
                description = "Real AI chat using saved API key and selected model.",
                enabled = true
            ),
            WorkspaceCapability(
                name = "Agent Prompt Builder",
                description = "Generate role-specific prompts for Android, UI, backend, debugging, and CI/CD.",
                enabled = true
            ),
            WorkspaceCapability(
                name = "Workflow Builder",
                description = "Create, reorder, simulate, and export multi-agent workflow prompts.",
                enabled = true
            ),
            WorkspaceCapability(
                name = "Terminal Logs",
                description = "Safe mobile command simulation and build-log copy system.",
                enabled = true
            ),
            WorkspaceCapability(
                name = "File Manager",
                description = "Browse project files, Stitch ZIP reference, workflows, and source files.",
                enabled = true
            ),
            WorkspaceCapability(
                name = "Marketplace",
                description = "Enable and disable models, tools, integrations, workflows, and agents.",
                enabled = true
            )
        )
    }
}

package com.mrrobot.aiworkspace.data

enum class WorkspaceFileType {
    Kotlin,
    Markdown,
    Gradle,
    Yaml,
    Zip,
    Folder,
    Text,
    Unknown
}

data class WorkspaceFile(
    val id: Long,
    val name: String,
    val path: String,
    val type: WorkspaceFileType,
    val size: String,
    val description: String,
    val contentPreview: String
)

object WorkspaceFiles {
    fun samples(): List<WorkspaceFile> {
        return listOf(
            WorkspaceFile(
                id = 1,
                name = "MainActivity.kt",
                path = "app/src/main/java/com/mrrobot/aiworkspace/MainActivity.kt",
                type = WorkspaceFileType.Kotlin,
                size = "1.2 KB",
                description = "Main Android entry point.",
                contentPreview = "class MainActivity : ComponentActivity() { ... }"
            ),
            WorkspaceFile(
                id = 2,
                name = "ChatScreen.kt",
                path = "app/src/main/java/com/mrrobot/aiworkspace/ui/screens/ChatScreen.kt",
                type = WorkspaceFileType.Kotlin,
                size = "9.8 KB",
                description = "OpenRouter-powered chat interface.",
                contentPreview = "@Composable fun ChatScreen(...) { ... }"
            ),
            WorkspaceFile(
                id = 3,
                name = "SettingsStore.kt",
                path = "app/src/main/java/com/mrrobot/aiworkspace/data/SettingsStore.kt",
                type = WorkspaceFileType.Kotlin,
                size = "2.1 KB",
                description = "DataStore persistence for API key and model.",
                contentPreview = "class SettingsStore(private val context: Context) { ... }"
            ),
            WorkspaceFile(
                id = 4,
                name = "build.gradle.kts",
                path = "app/build.gradle.kts",
                type = WorkspaceFileType.Gradle,
                size = "2.4 KB",
                description = "Android app Gradle configuration.",
                contentPreview = "plugins { id(\"com.android.application\") ... }"
            ),
            WorkspaceFile(
                id = 5,
                name = "build-apk.yml",
                path = ".github/workflows/build-apk.yml",
                type = WorkspaceFileType.Yaml,
                size = "1.9 KB",
                description = "GitHub Actions APK build workflow.",
                contentPreview = "name: Build Mr Robot AI Workspace APK"
            ),
            WorkspaceFile(
                id = 6,
                name = "BUILD_PROMPT.md",
                path = "BUILD_PROMPT.md",
                type = WorkspaceFileType.Markdown,
                size = "4.1 KB",
                description = "Main build instruction prompt.",
                contentPreview = "# Build Mr. Robot AI Workspace Android MVP"
            ),
            WorkspaceFile(
                id = 7,
                name = "stitch_mr._robot_ai_workspace.zip",
                path = "stitch_mr._robot_ai_workspace.zip",
                type = WorkspaceFileType.Zip,
                size = "Design ZIP",
                description = "Google Stitch source design export.",
                contentPreview = "Contains Stitch screen designs and references."
            )
        )
    }
}

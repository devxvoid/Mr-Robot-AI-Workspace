package com.mrrobot.aiworkspace.data

data class TerminalLine(
    val id: Long,
    val type: TerminalLineType,
    val text: String,
    val timestamp: String
)

enum class TerminalLineType {
    Command,
    Output,
    Success,
    Warning,
    Error,
    Info
}

object TerminalSamples {
    fun initialLogs(): List<TerminalLine> {
        return listOf(
            TerminalLine(
                id = 1,
                type = TerminalLineType.Info,
                text = "Mr. Robot terminal initialized.",
                timestamp = "00:00"
            ),
            TerminalLine(
                id = 2,
                type = TerminalLineType.Output,
                text = "Workspace ready. Type a command or use quick actions.",
                timestamp = "00:01"
            )
        )
    }
}

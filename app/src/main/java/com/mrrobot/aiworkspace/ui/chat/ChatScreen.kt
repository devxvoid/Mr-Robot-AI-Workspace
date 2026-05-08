package com.mrrobot.aiworkspace.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrrobot.aiworkspace.ai.OpenRouterModels
import com.mrrobot.aiworkspace.ui.markdown.MarkdownRenderer
import com.mrrobot.aiworkspace.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020617),
                        Color(0xFF071427),
                        Color(0xFF020617)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Text(
            text = "AI Workspace",
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Multi-session OpenRouter AI chat.",
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { vm.createSession() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("New Chat")
            }

            OutlinedButton(
                onClick = { vm.clearCurrentChat() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Clear")
            }

            OutlinedButton(
                onClick = { vm.deleteCurrentSession() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Delete")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            vm.sessions.forEach { session ->
                FilterChip(
                    selected = session.id == vm.selectedSessionId,
                    onClick = { vm.switchSession(session.id) },
                    label = {
                        Text(session.title)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = vm.apiKey,
            onValueChange = { vm.apiKey = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("OpenRouter API Key") },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = vm.selectedModel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("AI Model") },
                shape = RoundedCornerShape(18.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                OpenRouterModels.models.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model.title) },
                        onClick = {
                            vm.selectedModel = model.id
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(vm.currentMessages) { msg ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color =
                        if (msg.role == "user")
                            Color(0x2200D4FF)
                        else
                            Color(0x2200FFB2)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = msg.role.uppercase(),
                            color =
                                if (msg.role == "user")
                                    Color(0xFF00D4FF)
                                else
                                    Color(0xFF00FFB2),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        MarkdownRenderer(
                            markdown = msg.content
                        )
                    }
                }
            }
        }

        if (vm.loading) {
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF00D4FF)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.input,
            onValueChange = { vm.input = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Message") },
            minLines = 1,
            maxLines = 5,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { vm.sendMessage() },
            enabled = !vm.loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00D4FF),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = if (vm.loading) "Generating..." else "Send Message",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

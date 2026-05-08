package com.mrrobot.aiworkspace.ui.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownRenderer(
    markdown: String
) {

    val blocks =
        markdown.split("```")

    Column {

        blocks.forEachIndexed { index, block ->

            val isCode =
                index % 2 == 1

            if (isCode) {

                CodeBlock(block)

            } else {

                MarkdownText(block)
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }
    }
}

@Composable
fun MarkdownText(
    text: String
) {

    Text(
        text = text.trim(),
        color = Color.White,
        lineHeight = 24.sp,
        fontSize = 15.sp
    )
}

@Composable
fun CodeBlock(
    code: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF111827),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = code.trim(),
            color = Color(0xFF00FFB2),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 22.sp
        )
    }
}

package com.mrrobot.aiworkspace.ui.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
                modifier = Modifier.height(10.dp)
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
        fontSize = 15.sp,
        lineHeight = 24.sp
    )
}

@Composable
fun CodeBlock(
    raw: String
) {

    val code =
        raw.trim()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF111827),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {

        Text(
            text = "CODE",
            color = Color(0xFF00D4FF),
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text = highlight(code),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 22.sp
        )
    }
}

fun highlight(
    code: String
): AnnotatedString {

    val keywords = listOf(
        "fun",
        "class",
        "val",
        "var",
        "if",
        "else",
        "return",
        "import",
        "package",
        "object",
        "suspend",
        "data"
    )

    return buildAnnotatedString {

        val words =
            code.split(" ")

        words.forEach { word ->

            val clean =
                word.trim()

            if (
                keywords.contains(clean)
            ) {

                pushStyle(
                    SpanStyle(
                        color = Color(0xFF8B5CF6),
                        fontWeight = FontWeight.Bold
                    )
                )

                append(word)

                pop()

            } else if (
                clean.startsWith("\"")
            ) {

                pushStyle(
                    SpanStyle(
                        color = Color(0xFF00FFB2)
                    )
                )

                append(word)

                pop()

            } else {

                pushStyle(
                    SpanStyle(
                        color = Color(0xFFE5E7EB)
                    )
                )

                append(word)

                pop()
            }

            append(" ")
        }
    }
}

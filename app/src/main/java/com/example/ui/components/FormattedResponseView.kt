package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CodeBackground
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FormattedResponseView(
    content: String,
    onCopyCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val blocks = remember(content) { parseMarkdownBlocks(content) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is Block.Header -> {
                    Text(
                        text = block.text,
                        color = NeonCyan,
                        fontSize = when (block.level) {
                            1 -> 18.sp
                            2 -> 16.sp
                            else -> 15.sp
                        },
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }

                is Block.Code -> {
                    CodeBlockView(
                        language = block.language,
                        code = block.code,
                        onCopyCode = onCopyCode
                    )
                }

                is Block.Bullet -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = NeonPurple,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = formatInlineMarkdown(block.text),
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                is Block.Numbered -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${block.number}.",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = formatInlineMarkdown(block.text),
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                is Block.Paragraph -> {
                    if (block.text.isNotBlank()) {
                        Text(
                            text = formatInlineMarkdown(block.text),
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeBlockView(
    language: String,
    code: String,
    onCopyCode: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CodeBackground)
            .border(1.dp, Color(0xFF1B2647), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header with language and copy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" }.uppercase(),
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF141F3C))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(code))
                            onCopyCode(code)
                            copied = true
                            scope.launch {
                                delay(2000)
                                copied = false
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (copied) Color(0xFF10B981) else TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "Copied" else "Copy",
                        color = if (copied) Color(0xFF10B981) else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Code Text with horizontal scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = code,
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private sealed interface Block {
    data class Header(val level: Int, val text: String) : Block
    data class Code(val language: String, val code: String) : Block
    data class Bullet(val text: String) : Block
    data class Numbered(val number: String, val text: String) : Block
    data class Paragraph(val text: String) : Block
}

private fun parseMarkdownBlocks(markdown: String): List<Block> {
    val lines = markdown.lines()
    val blocks = mutableListOf<Block>()
    var inCodeBlock = false
    var codeLang = ""
    val codeBuilder = StringBuilder()

    for (line in lines) {
        val trimmed = line.trim()

        if (trimmed.startsWith("```")) {
            if (inCodeBlock) {
                blocks.add(Block.Code(codeLang, codeBuilder.toString().trimEnd()))
                codeBuilder.clear()
                inCodeBlock = false
            } else {
                inCodeBlock = true
                codeLang = trimmed.removePrefix("```").trim()
            }
            continue
        }

        if (inCodeBlock) {
            codeBuilder.appendLine(line)
            continue
        }

        when {
            trimmed.startsWith("### ") -> blocks.add(Block.Header(3, trimmed.removePrefix("### ")))
            trimmed.startsWith("## ") -> blocks.add(Block.Header(2, trimmed.removePrefix("## ")))
            trimmed.startsWith("# ") -> blocks.add(Block.Header(1, trimmed.removePrefix("# ")))
            trimmed.startsWith("- ") -> blocks.add(Block.Bullet(trimmed.removePrefix("- ")))
            trimmed.startsWith("* ") -> blocks.add(Block.Bullet(trimmed.removePrefix("* ")))
            trimmed.matches(Regex("^\\d+\\.\\s+.*")) -> {
                val match = Regex("^(\\d+)\\.\\s+(.*)").find(trimmed)
                if (match != null) {
                    blocks.add(Block.Numbered(match.groupValues[1], match.groupValues[2]))
                } else {
                    blocks.add(Block.Paragraph(trimmed))
                }
            }
            trimmed.isNotBlank() -> blocks.add(Block.Paragraph(trimmed))
        }
    }

    if (inCodeBlock && codeBuilder.isNotEmpty()) {
        blocks.add(Block.Code(codeLang, codeBuilder.toString().trimEnd()))
    }

    return blocks
}

private fun formatInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        var lastIndex = 0

        for (match in boldRegex.findAll(text)) {
            val range = match.range
            if (range.first > lastIndex) {
                append(text.substring(lastIndex, range.first))
            }
            val boldContent = match.groupValues[1]
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NeonCyan))
            append(boldContent)
            pop()
            lastIndex = range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}

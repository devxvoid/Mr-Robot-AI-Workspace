from pathlib import Path
import re


CHAT_SCREEN = Path("app/src/main/java/com/mrrobot/aiworkspace/ui/screens/ChatScreen.kt")
MANIFEST = Path("app/src/main/AndroidManifest.xml")
PLUS_ICON = Path("app/src/main/res/drawable/ic_lucide_plus.xml")
MIC_ICON = Path("app/src/main/res/drawable/ic_lucide_mic.xml")


def require_file(path: Path) -> None:
    if not path.exists():
        raise SystemExit(f"Missing required file: {path}")


def ensure_import(text: str, import_line: str, anchor: str) -> str:
    if import_line in text:
        return text

    if anchor not in text:
        raise SystemExit(f"Import anchor not found for {import_line}: {anchor!r}")

    return text.replace(anchor, anchor + import_line + "\n", 1)


def write_icons() -> None:
    PLUS_ICON.parent.mkdir(parents=True, exist_ok=True)

    PLUS_ICON.write_text(
        """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M5,12H19"
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFFFF"
        android:strokeWidth="2.2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round" />
    <path
        android:pathData="M12,5V19"
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFFFF"
        android:strokeWidth="2.2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round" />
</vector>
"""
    )

    MIC_ICON.write_text(
        """<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M12,2C10.343,2 9,3.343 9,5V12C9,13.657 10.343,15 12,15C13.657,15 15,13.657 15,12V5C15,3.343 13.657,2 12,2Z"
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFFFF"
        android:strokeWidth="2.1"
        android:strokeLineCap="round"
        android:strokeLineJoin="round" />
    <path
        android:pathData="M19,10V12C19,15.866 15.866,19 12,19C8.134,19 5,15.866 5,12V10"
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFFFF"
        android:strokeWidth="2.1"
        android:strokeLineCap="round"
        android:strokeLineJoin="round" />
    <path
        android:pathData="M12,19V22"
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFFFF"
        android:strokeWidth="2.1"
        android:strokeLineCap="round"
        android:strokeLineJoin="round" />
    <path
        android:pathData="M8,22H16"
        android:fillColor="@android:color/transparent"
        android:strokeColor="#FFFFFFFF"
        android:strokeWidth="2.1"
        android:strokeLineCap="round"
        android:strokeLineJoin="round" />
</vector>
"""
    )


def patch_chat_screen() -> None:
    require_file(CHAT_SCREEN)

    text = CHAT_SCREEN.read_text()
    original = text

    text = ensure_import(
        text,
        "import android.app.Activity",
        "package com.mrrobot.aiworkspace.ui.screens\n\n",
    )

    text = ensure_import(
        text,
        "import android.content.Intent",
        "import androidx.compose.foundation.background\n",
    )

    text = ensure_import(
        text,
        "import android.speech.RecognizerIntent",
        "import android.content.Intent\n",
    )

    text = ensure_import(
        text,
        "import android.widget.Toast",
        "import android.speech.RecognizerIntent\n",
    )

    text = ensure_import(
        text,
        "import androidx.compose.ui.platform.LocalContext",
        "import androidx.compose.ui.platform.LocalClipboardManager\n",
    )

    text = ensure_import(
        text,
        "import androidx.compose.ui.res.painterResource",
        "import androidx.compose.ui.platform.LocalContext\n",
    )

    text = ensure_import(
        text,
        "import com.mrrobot.aiworkspace.R",
        "import androidx.lifecycle.viewmodel.compose.viewModel\n",
    )

    text = ensure_import(
        text,
        "import java.util.Locale",
        "import kotlinx.coroutines.launch\n",
    )

    # Add context + speech launcher inside ChatScreen().
    if "val speechLauncher = rememberLauncherForActivityResult" not in text:
        marker = """    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

"""

        replacement = """    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
                .trim()

            if (spokenText.isNotBlank()) {
                val currentInput = state.input.trim()

                val newInput = if (currentInput.isBlank()) {
                    spokenText
                } else {
                    "$currentInput $spokenText"
                }

                viewModel.updateInput(newInput)
            }
        }
    }

"""

        if marker not in text:
            raise SystemExit("Could not find ChatScreen state/list/scope marker.")

        text = text.replace(marker, replacement, 1)

    # Add onMicClick argument to PromptInputBar call.
    if "onMicClick = {" not in text:
        marker = """            onInputChange = viewModel::updateInput,
            onSend = { viewModel.send() },
"""

        replacement = """            onInputChange = viewModel::updateInput,
            onMicClick = {
                launchSpeechInput(
                    context = context,
                    launcher = speechLauncher::launch
                )
            },
            onSend = { viewModel.send() },
"""

        if marker not in text:
            raise SystemExit("Could not find PromptInputBar onInputChange/onSend marker.")

        text = text.replace(marker, replacement, 1)

    # Add onMicClick parameter to PromptInputBar signature.
    if "onMicClick: () -> Unit" not in text:
        marker = """    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
"""

        replacement = """    canRegenerate: Boolean,
    onInputChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onSend: () -> Unit,
"""

        if marker not in text:
            raise SystemExit("Could not find PromptInputBar signature marker.")

        text = text.replace(marker, replacement, 1)

    # Replace old single full-width text field with premium row containing mic button.
    old_text_field = """    OutlinedTextField(
        value = input,
        onValueChange = onInputChange,
        placeholder = { Text("Ask Mr. Robot...") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 1,
        maxLines = 5
    )

"""

    new_text_field = """    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                color = NeonCyan.copy(alpha = 0.14f),
                border = BorderStroke(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = 0.35f)
                )
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lucide_plus),
                        contentDescription = "Attach files",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(18.dp),
                color = NeonCyan.copy(alpha = 0.14f),
                border = BorderStroke(
                    width = 1.dp,
                    color = NeonCyan.copy(alpha = 0.35f)
                )
            ) {
                IconButton(onClick = onMicClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lucide_mic),
                        contentDescription = "Voice input",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { Text("Ask Mr. Robot...") },
                modifier = Modifier.weight(1f),
                minLines = 1,
                maxLines = 5,
                shape = RoundedCornerShape(18.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
    }

"""

    if old_text_field in text:
        text = text.replace(old_text_field, new_text_field, 1)
    elif "contentDescription = \"Voice input\"" not in text:
        raise SystemExit("Could not find old OutlinedTextField block to replace.")

    # Add speech helper above PromptInputBar or at bottom before final functions.
    if "private fun launchSpeechInput(" not in text:
        helper = """
private fun launchSpeechInput(
    context: Context,
    launcher: (Intent) -> Unit
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Mr. Robot")
    }

    runCatching {
        launcher(intent)
    }.onFailure {
        Toast.makeText(
            context,
            "Voice input is not available on this device.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

"""

        marker = "\n@Composable\nprivate fun PromptInputBar(\n"

        if marker not in text:
            raise SystemExit("Could not find PromptInputBar marker to insert launchSpeechInput.")

        text = text.replace(marker, helper + marker, 1)

    CHAT_SCREEN.write_text(text)

    if text == original:
        print("ChatScreen.kt already patched.")
    else:
        print("Patched ChatScreen.kt")


def patch_manifest() -> None:
    require_file(MANIFEST)

    text = MANIFEST.read_text()
    original = text

    if "android.permission.RECORD_AUDIO" not in text:
        match = re.search(r"<manifest\\b[^>]*>", text)

        if not match:
            raise SystemExit("Could not find opening <manifest> tag.")

        text = (
            text[: match.end()]
            + '\n    <uses-permission android:name="android.permission.RECORD_AUDIO" />'
            + text[match.end() :]
        )

    MANIFEST.write_text(text)

    if text == original:
        print("AndroidManifest.xml already had RECORD_AUDIO permission.")
    else:
        print("Patched AndroidManifest.xml")


def main() -> None:
    write_icons()
    patch_chat_screen()
    patch_manifest()


if __name__ == "__main__":
    main()

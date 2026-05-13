from pathlib import Path
import re


CHAT_SCREEN = Path("app/src/main/java/com/mrrobot/aiworkspace/ui/screens/ChatScreen.kt")
MANIFEST = Path("app/src/main/AndroidManifest.xml")


def require_file(path: Path) -> None:
    if not path.exists():
        raise SystemExit(f"Missing required file: {path}")


def ensure_import(text: str, import_line: str, anchor: str) -> str:
    if import_line in text:
        return text

    if anchor not in text:
        raise SystemExit(f"Import anchor not found for {import_line}: {anchor!r}")

    return text.replace(anchor, anchor + import_line + "\n", 1)


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
        "import android.content.Context\n",
    )

    text = ensure_import(
        text,
        "import android.speech.RecognizerIntent",
        "import android.provider.OpenableColumns\n",
    )

    text = ensure_import(
        text,
        "import android.widget.Toast",
        "import android.speech.RecognizerIntent\n",
    )

    text = ensure_import(
        text,
        "import java.util.Locale",
        "import java.io.InputStreamReader\n",
    )

    if "val speechLauncher = rememberLauncherForActivityResult" not in text:
        marker = "              LaunchedEffect(state.messages.size, state.isLoading) {"

        speech_launcher = '''              val speechLauncher = rememberLauncherForActivityResult(
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

'''

        if marker not in text:
            raise SystemExit("Could not find LaunchedEffect marker in ChatScreen.kt")

        text = text.replace(marker, speech_launcher + marker, 1)

    if "onMicClick = {" not in text:
        target = '''                      onPickFiles = {
                          attachmentLauncher.launch(
                              arrayOf(
                                  "text/*",
                                  "image/*",
                                  "application/pdf",
                                  "application/json",
                                  "application/xml",
                                  "application/zip",
                                  "*/*"
                              )
                          )
                      },
'''

        replacement = '''                      onPickFiles = {
                          attachmentLauncher.launch(
                              arrayOf(
                                  "text/*",
                                  "image/*",
                                  "application/pdf",
                                  "application/json",
                                  "application/xml",
                                  "application/zip",
                                  "*/*"
                              )
                          )
                      },
                      onMicClick = {
                          launchSpeechInput(
                              context = context,
                              launcher = speechLauncher::launch
                          )
                      },
'''

        if target not in text:
            raise SystemExit("Could not find onPickFiles block in ChatScreen.kt")

        text = text.replace(target, replacement, 1)

    if "onMicClick: () -> Unit" not in text:
        target = '''              onInputChange: (String) -> Unit,
              onPickFiles: () -> Unit,
              onRemoveAttachment: (ChatAttachment) -> Unit,
'''

        replacement = '''              onInputChange: (String) -> Unit,
              onPickFiles: () -> Unit,
              onMicClick: () -> Unit,
              onRemoveAttachment: (ChatAttachment) -> Unit,
'''

        if target not in text:
            raise SystemExit("Could not find PremiumPromptInputBar signature in ChatScreen.kt")

        text = text.replace(target, replacement, 1)

    text = text.replace(
        '''                      PremiumIconButton(
                          icon = R.drawable.ic_lucide_mic,
                          contentDescription = "Voice input",
                          onClick = {}
                      )
''',
        '''                      PremiumIconButton(
                          icon = R.drawable.ic_lucide_mic,
                          contentDescription = "Voice input",
                          onClick = onMicClick
                      )
''',
    )

    text = re.sub(
        r'(PremiumIconButton\(\s*icon\s*=\s*R\.drawable\.ic_lucide_mic,\s*contentDescription\s*=\s*"Voice input",\s*)onClick\s*=\s*\{\s*\}(\s*\))',
        r'\1onClick = onMicClick\2',
        text,
        flags=re.DOTALL,
        count=1,
    )

    if "private fun launchSpeechInput(" not in text:
        helper = '''          private fun launchSpeechInput(
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

'''

        marker = "          private fun createAttachmentFromUri(\n"

        if marker not in text:
            raise SystemExit("Could not find createAttachmentFromUri marker in ChatScreen.kt")

        text = text.replace(marker, helper + marker, 1)

    CHAT_SCREEN.write_text(text)

    if text == original:
        print("ChatScreen.kt already had the mic restore patch.")
    else:
        print("Patched ChatScreen.kt")


def patch_manifest() -> None:
    require_file(MANIFEST)

    text = MANIFEST.read_text()
    original = text

    if "android.permission.RECORD_AUDIO" not in text:
        match = re.search(r"<manifest\\b[^>]*>", text)

        if not match:
            raise SystemExit("Could not find opening <manifest> tag")

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
    patch_chat_screen()
    patch_manifest()


if __name__ == "__main__":
    main()

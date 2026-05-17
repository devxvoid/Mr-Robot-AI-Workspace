# Step 46 Fix Chat Attachment Provider Compatibility

This step fixes the final validation compile error caused by ChatScreen expecting attachment APIs that were removed from ChatViewModel.

## Fixed

- Restores `ChatAttachment`.
- Restores `selectedAttachments`.
- Restores `addAttachments`.
- Restores `removeAttachment`.
- Restores `useSuggestion`.
- Restores message-level `attachments`.
- Keeps provider-aware chat routing.
- Stops AI Chat from requiring OpenRouter when Anthropic Claude, OpenAI, Gemini, Groq, Mistral, DeepSeek, or xAI is active.
- Adds INTERNET permission if missing.

## Expected behavior

- Final validation should compile.
- AI Chat should use the active provider from Settings.
- Anthropic Claude should not trigger OpenRouter missing-key errors.

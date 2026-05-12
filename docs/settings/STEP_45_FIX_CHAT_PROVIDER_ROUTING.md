# Step 45 Fix Chat Provider Routing

This step fixes AI Chat using OpenRouter-only logic.

## Fixed

- AI Chat now reads the active provider from Settings.
- AI Chat now uses `settings.activeApiKey()`.
- AI Chat now uses `settings.activeModel()`.
- AI Chat no longer requires OpenRouter when Anthropic, OpenAI, Gemini, Groq, Mistral, DeepSeek, or xAI is active.
- `ChatRepository` now routes through `ProviderChatClient`.
- Anthropic Claude uses the official `/v1/messages` endpoint.
- Gemini uses the official `generateContent` endpoint.
- OpenAI-compatible providers use `/chat/completions`.

## Expected behavior

- Add Anthropic API key.
- Select Claude model.
- Tap Save & Activate.
- Open AI Chat.
- Status should show Anthropic Claude ready.
- Sending a message should call Anthropic, not OpenRouter.

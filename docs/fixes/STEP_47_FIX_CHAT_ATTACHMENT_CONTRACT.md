# Step 47 Fix Chat Attachment Contract

Fixed latest final validation errors:

- Restored `imageDataUrl` on `ChatAttachment`.
- Restored `extractionStatus` on `ChatAttachment`.
- Changed `ChatUiMessage.id` to `String` for UUID based message IDs.
- Added constructor compatibility for both string-first and URI-first attachment creation.
- Removed `removeAttachment` callable ambiguity by keeping one public attachment remover.
- Kept provider-aware runtime routing so Anthropic Claude does not ask for OpenRouter.
- Added Cyber and Hacker handling in theme mode.
- Ensured INTERNET permission exists.

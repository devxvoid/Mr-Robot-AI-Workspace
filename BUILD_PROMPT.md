# Build Mr. Robot AI Workspace Android MVP

Use the uploaded Google Stitch export ZIP as the visual source.

The ZIP contains:
- design.md
- welcome
- login
- ai_chat
- agents
- workflow_builder
- live_terminal
- file_manager
- marketplace
- settings
- profile
- hacker_mode variants

Build a complete Android MVP using:

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- MVVM
- Coroutines
- DataStore
- Retrofit/OkHttp
- OpenRouter API

App name:
Mr. Robot AI Workspace

Package:
com.mrrobot.aiworkspace

Required screens:
1. WelcomeScreen
2. LoginScreen
3. ChatScreen
4. AgentsScreen
5. WorkflowScreen
6. TerminalScreen
7. FileManagerScreen
8. MarketplaceScreen
9. SettingsScreen
10. ProfileScreen

MVP functionality:
- App launches successfully
- Navigation works between all screens
- Premium dark cyberpunk UI matching Stitch design
- Settings screen lets user enter OpenRouter API key
- API key is saved locally using DataStore
- Chat screen sends messages to OpenRouter
- Model selector supports:
  - openai/gpt-4o-mini
  - google/gemini-2.0-flash-001
  - anthropic/claude-3.5-sonnet
  - deepseek/deepseek-chat
- Chat has loading, error, and empty states
- Agents screen shows mock AI agents
- Workflow builder shows mock automation steps
- Live terminal shows mock logs
- File manager shows mock files
- Marketplace shows mock models/tools
- Profile and settings are functional UI screens

UI requirements:
- Use Stitch screenshots/code as visual reference
- Dark background
- Neon green/blue/purple accents
- Glassmorphism cards
- Rounded panels
- Premium spacing
- Smooth animations
- Material 3 polish
- Mobile-first layout
- No broken placeholder screens

Project requirements:
- Full Gradle Android project
- Include Gradle wrapper
- Java 17
- Build using ./gradlew assembleDebug
- Add GitHub Actions workflow
- Upload APK artifact
- Include build logs
- No missing imports
- No broken files
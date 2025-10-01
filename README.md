# Wellness App - Android Mobile Application

A comprehensive wellness tracking application built with Kotlin and Android Studio.

## Features

1. **Daily Habit Tracker** - Add, edit, delete daily wellness habits with completion progress
2. **Mood Journal with Emoji Selector** - Log mood entries with date/time and emojis
3. **Hydration Reminder** - Configurable notifications to remind users to drink water
4. **Home-screen Widget** - Shows today's habit completion percentage
5. **Data Persistence** - Uses SharedPreferences to store all user data

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/wellness/app/
│   │   │   ├── MainActivity.kt
│   │   │   ├── models/
│   │   │   │   ├── Habit.kt
│   │   │   │   └── MoodEntry.kt
│   │   │   ├── fragments/
│   │   │   │   ├── HabitsFragment.kt
│   │   │   │   ├── MoodJournalFragment.kt
│   │   │   │   └── SettingsFragment.kt
│   │   │   ├── adapters/
│   │   │   │   ├── HabitAdapter.kt
│   │   │   │   └── MoodAdapter.kt
│   │   │   ├── utils/
│   │   │   │   ├── DataManager.kt
│   │   │   │   └── NotificationHelper.kt
│   │   │   ├── widget/
│   │   │   │   └── HabitWidgetProvider.kt
│   │   │   └── receiver/
│   │   │       └── HydrationReceiver.kt
│   │   └── res/
│   │       ├── layout/
│   │       ├── values/
│   │       ├── drawable/
│   │       └── xml/
│   └── AndroidManifest.xml
└── build.gradle
```

## Setup Instructions

1. **Extract the ZIP file**
2. **Open Android Studio**
3. **Select "Open an Existing Project"**
4. **Navigate to the extracted folder and select it**
5. **Wait for Gradle sync to complete**
6. **Run the app on an emulator or physical device**

## Technical Requirements Met

- ✅ Architecture: Uses Fragments and Activities
- ✅ Data Persistence: SharedPreferences with Gson
- ✅ Intents: Explicit for navigation, implicit for sharing
- ✅ State Management: Settings retained across sessions
- ✅ Responsive UI: Adapts to phones/tablets, portrait/landscape
- ✅ Notifications: AlarmManager for hydration reminders
- ✅ Widget: Home-screen widget showing habit completion

## Dependencies

All required dependencies are included in `build.gradle`:
- Material Design Components
- Gson for JSON serialization
- CardView for UI components
- RecyclerView for lists

## Minimum SDK Version

- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 34 (Android 14)

## Permissions Required

- `POST_NOTIFICATIONS` - For hydration reminders
- `SCHEDULE_EXACT_ALARM` - For precise timing of reminders
- `RECEIVE_BOOT_COMPLETED` - To restore reminders after reboot

## How to Use

### Habits Tab
- Tap the **+** button to add a new habit
- Tap checkbox to mark habit as complete for today
- Long press or tap edit icon to modify habits
- View weekly completion progress for each habit

### Mood Journal Tab
- Tap the **+** button to log your mood
- Select an emoji that represents your current mood
- Add optional notes
- View history of past moods
- Share mood entries via any app

### Settings Tab
- Enable/disable hydration reminders
- Set reminder interval (minimum 15 minutes)
- Customize notification preferences

### Widget
- Long press home screen → Widgets
- Find "Wellness Habit Widget"
- Drag to home screen
- Shows real-time habit completion percentage

## Color Scheme

The app uses a modern, calming color palette:
- Primary: Teal (#009688)
- Secondary: Amber (#FFC107)
- Accent: Deep Orange (#FF5722)
- Background: Light Gray (#F5F5F5)

## Author

Created for IT2010 - Mobile Application Development
SLIIT 2025 - Lab Exam 03

## License

Educational project for academic purposes.

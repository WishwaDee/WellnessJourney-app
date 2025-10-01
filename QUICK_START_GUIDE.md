# Wellness App - Quick Start Guide

## Download Link
**Download the compressed file: `WellnessApp-Android.tar.gz`**

## How to Import into Android Studio

### Step 1: Extract the Archive
1. Download `WellnessApp-Android.tar.gz` from this project
2. Extract it to your desired location:
   - **Windows**: Use 7-Zip or WinRAR
   - **Mac/Linux**: Double-click or use terminal: `tar -xzf WellnessApp-Android.tar.gz`

### Step 2: Open in Android Studio
1. Launch **Android Studio**
2. Click **"Open"** (not "New Project")
3. Navigate to the extracted `android-wellness-app` folder
4. Click **"OK"**

### Step 3: Wait for Gradle Sync
- Android Studio will automatically sync Gradle dependencies
- This may take 2-5 minutes on first import
- Wait for "Gradle build finished" message

### Step 4: Run the App
1. Connect an Android device or start an emulator
2. Click the green **"Run"** button (▶️)
3. Select your device/emulator
4. Wait for the app to install and launch

## Features Overview

### 1. Habits Tab
- **Add Habit**: Click the + button
- **Complete Habit**: Tap the checkbox
- **Edit Habit**: Click the edit icon
- **Delete Habit**: Long press on a habit
- **View Progress**: See weekly completion percentage

### 2. Mood Journal Tab
- **Log Mood**: Click the + button
- **Select Emoji**: Choose from 10 mood options
- **Add Note**: Optional mood description
- **View History**: Scroll through past moods
- **Share/Delete**: Tap on any mood entry

### 3. Settings Tab
- **Enable Reminders**: Toggle the switch
- **Set Interval**: Enter minutes (minimum 15)
- **Save**: Click "Save Settings" button

### 4. Home Screen Widget
1. Long press on home screen
2. Tap "Widgets"
3. Find "Wellness Habit Widget"
4. Drag to home screen
5. Shows real-time habit completion percentage

## Technical Details

### Requirements
- **Android Studio**: Arctic Fox or newer
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin Version**: 1.9.20

### Key Technologies
- Kotlin
- Material Design Components
- RecyclerView
- SharedPreferences
- AlarmManager
- App Widget
- Fragments

### Permissions
- `POST_NOTIFICATIONS` - For hydration reminders
- `SCHEDULE_EXACT_ALARM` - For precise timing
- `RECEIVE_BOOT_COMPLETED` - To restore reminders after reboot

## Project Structure

```
app/
├── src/main/
│   ├── java/com/wellness/app/
│   │   ├── MainActivity.kt                 (Main entry point)
│   │   ├── models/
│   │   │   ├── Habit.kt                   (Habit data model)
│   │   │   └── MoodEntry.kt               (Mood data model)
│   │   ├── fragments/
│   │   │   ├── HabitsFragment.kt          (Habits screen)
│   │   │   ├── MoodJournalFragment.kt     (Mood journal screen)
│   │   │   └── SettingsFragment.kt        (Settings screen)
│   │   ├── adapters/
│   │   │   ├── HabitAdapter.kt            (RecyclerView adapter)
│   │   │   └── MoodAdapter.kt             (RecyclerView adapter)
│   │   ├── utils/
│   │   │   ├── DataManager.kt             (Data persistence)
│   │   │   └── NotificationHelper.kt      (Notifications)
│   │   ├── widget/
│   │   │   └── HabitWidgetProvider.kt     (Home screen widget)
│   │   └── receiver/
│   │       └── HydrationReceiver.kt       (Alarm receiver)
│   ├── res/
│   │   ├── layout/                        (All XML layouts)
│   │   ├── values/                        (Colors, strings, themes)
│   │   ├── menu/                          (Bottom navigation menu)
│   │   └── xml/                           (Widget configuration)
│   └── AndroidManifest.xml                (App configuration)
└── build.gradle                           (App dependencies)
```

## Troubleshooting

### Gradle Sync Failed
- Ensure you have a stable internet connection
- Go to **File → Invalidate Caches → Invalidate and Restart**
- Update Android Studio to the latest version

### App Won't Run
- Check that your device/emulator is running Android 7.0 or higher
- Enable USB debugging on physical devices
- Try cleaning the project: **Build → Clean Project**

### Notifications Not Working
- Ensure notification permissions are granted
- Check device notification settings
- On Android 13+, you must manually grant notification permission

### Widget Not Updating
- Widgets update every 30 minutes by default
- Tap the widget to manually refresh
- Check that the app is not being battery-optimized

## Meeting Lab Requirements

✅ **Daily Habit Tracker** - Add, edit, delete habits with progress tracking
✅ **Mood Journal with Emoji Selector** - 10 emoji options with date/time
✅ **Hydration Reminder** - AlarmManager with configurable intervals
✅ **Home-screen Widget** - Shows today's habit completion percentage
✅ **Architecture** - Uses Fragments for Habits, Mood Journal, Settings
✅ **Data Persistence** - SharedPreferences with Gson
✅ **Intents** - Explicit for navigation, implicit for sharing moods
✅ **State Management** - Settings retained across sessions
✅ **Responsive UI** - Adapts to all screen sizes and orientations

## Color Scheme
- **Primary**: Teal (#009688)
- **Accent**: Deep Orange (#FF5722)
- **Background**: Light Gray (#F5F5F5)

## Testing the App

1. **Habits**: Create 3-5 habits and mark some as complete
2. **Moods**: Log different moods throughout the day
3. **Reminders**: Set a short interval (15 min) to test notifications
4. **Widget**: Add widget to home screen and verify it updates
5. **Rotation**: Rotate device to test landscape mode
6. **Data Persistence**: Close and reopen app to verify data is saved

## Support

For issues or questions:
- Check the README.md file
- Review the code comments
- Consult Android Developer documentation

## License
Educational project for IT2010 - SLIIT 2025

---

**Good luck with your lab exam!**

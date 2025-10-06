# Wellness Journey App

## Project Overview
Wellness Journey is an Android wellness companion that helps users build healthy routines with daily habits, mood journaling, and hydration tracking. The app persists all user-generated content with `SharedPreferences`, supports both phones and tablets, and is optimized for light/dark themes in portrait and landscape orientations.

## Feature Coverage
The application satisfies the required capstone features through the components outlined below:

### 1. Daily Habit Tracker
- `HabitsFragment` allows users to add, edit, delete, and mark completion of daily wellness habits with a floating action button and contextual dialogs.
- Habit data and completion history are persisted via `DataManager`, enabling progress summaries and streak tracking on both the habits screen and dashboard widgets.

### 2. Mood Journal with Emoji Selector
- `MoodJournalFragment` presents curated emoji chips that let users log their mood, add optional notes, review recent entries, and remove past logs.
- Mood entries are stored with timestamps in `DataManager`, powering dashboard mood summaries and ensuring historical records survive app restarts.

### 3. Hydration Reminder
- `HydrationFragment` tracks water intake, provides quick-add buttons, and visualizes progress toward a configurable hydration goal.
- Notification scheduling is handled by `NotificationHelper`, which uses WorkManager to deliver repeating reminders at user-selected intervals from `SettingsFragment`.

### 4. Advanced Feature – Home Screen Widget
- `HabitWidgetProvider` exposes an app widget that surfaces the current day's habit completion percentage and deep-links into the main experience, fulfilling the advanced feature requirement.

## Additional Implementation Notes
- Shared data models (`Habit`, `MoodEntry`, `HydrationEntry`) encapsulate unique identifiers to support editing and deletion workflows.
- `DataManager` centralizes persistence, date handling, and analytics across modules, simplifying feature updates and consistency checks.

## Testing
Run `./gradlew lint` to execute static analysis. (The command may require a local Android SDK installation.)

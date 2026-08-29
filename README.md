# 📱 Prime Tracker
A native Android companion app for Warframe players.

[![Android](https://img.shields.io/badge/Android-34%2B-3DDC84?logo=android&logoColor=white)](...)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?logo=kotlin&logoColor=white)](...)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](...)
[![License](https://img.shields.io/github/license/FelipiMatheuz/prime-tracker-android)](...)
![Status](https://img.shields.io/badge/status-active-green)

## ✨ Overview

**Prime Tracker** (previously **Warframe Prime Hunt**) is a native Android companion app for Warframe players to manage their Prime collection, track their inventory, explore Relics and organize farming goals.

The app separates what you own from what you want to accomplish. Inventory automatically determines Prime Set completion, while Goals provide a flexible way to plan farming, trading and other objectives.

This is also a **public open-source project** aimed at continuous improvement through community collaboration.

---

## 🚀 Key Features

### Prime Collection
- Browse Prime Sets and Prime Parts
- Track owned quantities through Inventory
- Automatic Prime Set completion
- Search and advanced filtering
- Duplicate tracking

### Relics
- Browse the complete Relic database
- View drop tables
- Availability and acquisition source
- Search and filtering

### Goals
- Create custom farming and collection objectives
- Track desired quantities
- Organize objectives with custom Tags
- Add notes and duplicate Goals
- Separate planning from Inventory

### Overview
- Live statistical overview of your collection
- Prime Set completion statistics
- Inventory/database statistics
- Relic availability overview
- Goal progress summary

### Cloud
- Sign in with the existing cloud service
- Upload personal data
- Download personal data
- Legacy checklist migration (limited time)
- Clear cloud data

---

## 🏗️ Architecture

The Android application follows a layered architecture with clear
separation between UI, domain logic and data access.

The application is designed around:

- Local-first personal data
- Reactive state with Kotlin Flow
- Repository-based data access
- Independent game-data and user-data pipelines
- Automated game database updates

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVI
- **Database:** DataPreferences and Room (Local Storage), Firebase (Backup and remote storage)
- **Libraries & Tools:** 
  - Android Jetpack (Compose, Navigation3)
  - ViewModel
  - Kotlin Flow & Coroutines
  - Room
  - Coil 3 (image loading)
  - Firebase (Auth, Firestore)
  - Material3 Components
  - Retrofit2 (API calls)
- **Min SDK:** Android 8.1 (API 27)

---

## 🤝 Contributing

Contributions are welcome.

Before opening a Pull Request:

1. Check existing Issues.
2. Fork the repository.
3. Create a feature or fix branch.
4. Make your changes.
5. Run the available checks and tests.
6. Open a Pull Request describing the change.

---

## ❤️ Support the Project

Prime Tracker is free and open source.

If Prime Tracker has been useful to you, consider supporting its
continued development.

Your contribution helps maintain the game database, develop new
features and keep the project available for the community.

Email: cyberman.studios@gmail.com

---

## 🧾 License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.
See the [LICENSE](LICENSE) file for more information.

---

## 📢 Credits

Prime Tracker is an independent community project for Warframe players.

Warframe™, Prime, Relics and related names, assets and trademarks
belong to Digital Extremes Ltd.

Prime Tracker is not affiliated with or endorsed by Digital Extremes.

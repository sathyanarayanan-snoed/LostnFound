
# LostnFound — Campus Lost & Found Platform

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore%20%7C%20Storage-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)

**LostnFound** is a native Android application built with **Kotlin** and **Jetpack Compose** that serves as a digital bulletin board for reporting, discovering, and recovering lost and found items across campus. Designed with **Material 3** and a clean layered architecture, the app combines map-based location tagging, cloud synchronization, and an automated **7-day post expiration policy** to keep listings relevant and clutter-free.

---

## ✨ Key Features

* **Campus Email Authentication:** Secure sign-in and registration workflow powered by Firebase Authentication.
* **Lost & Found Feed:** Browse real-time listings categorized by *Lost* and *Found* status with search and filtering capabilities.
* **Item Reporting & CRUD Operations:** Create detailed item posts with photos, descriptions, category tags, and timestamps, or mark items as resolved once recovered.
* **Map-Based Location Pinning:** Interactive campus location selection and viewing powered by **Google Maps Compose**.
* **7-Day Auto-Expiration Policy:** Utilizes **Firebase Firestore TTL (Time-to-Live)** policies to automatically purge expired listings after 7 days, ensuring the board stays active without manual cleanup.
* **Push Notifications:** Real-time alerts for newly reported items and status updates via Firebase Cloud Messaging (FCM).
* **Dual Repository Mode (Mock & Live Firebase):** Includes out-of-the-box mock repository implementations alongside Firebase repositories so the app can be built, demonstrated, and tested immediately without requiring external cloud credentials.

---

## 🛠️ Tech Stack & Libraries

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 Design |
| **Architecture** | Layered Clean Architecture (`data`, `domain`, `ui`) + MVVM |
| **Navigation** | Jetpack Navigation Compose (Type-Safe Navigation) |
| **Backend & Cloud** | Firebase Auth, Cloud Firestore (with TTL), Firebase Storage, FCM |
| **Maps & Location** | Google Maps Compose SDK |
| **Image Loading** | Coil (`coil-compose`) |
| **Asynchronous Programming** | Kotlin Coroutines & `StateFlow` |

---

## 📂 Project Structure

```text
app/src/main/java/
├── data/
│   ├── repository/       # Firebase & Mock repository implementations (Auth, Item, Storage)
│   └── remote/           # Firestore, Storage, and FCM data sources
├── domain/
│   ├── model/            # Core data models (Item, User, LocationPin, ItemStatus)
│   └── repository/       # Repository interfaces
└── ui/
    ├── navigation/       # Type-safe navigation graph and route definitions
    ├── screens/
    │   ├── auth/         # Login and Registration screens
    │   ├── feed/         # Main Lost & Found bulletin feed
    │   ├── detail/       # Item Detail screen with Map preview & contact actions
    │   ├── post/         # Create / Edit Lost or Found item screen
    │   └── profile/      # User Profile and user-submitted listings management
    ├── components/       # Reusable Material 3 Compose UI components
    └── theme/            # Color schemes, typography, and Material 3 theme setup

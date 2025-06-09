# 🧠 MemeForge

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blueviolet?logo=android)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Auth-yellow?logo=firebase)](https://firebase.google.com/)
[![Room DB](https://img.shields.io/badge/Room-Offline-green?logo=sqlite)](https://developer.android.com/training/data-storage/room)
[![Gemini AI](https://img.shields.io/badge/Gemini-AI-blue?logo=google)](https://ai.google.dev/)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

MemeForge is a powerful and intelligent meme generator app for Android, designed using modern development tools. It allows users to browse, edit, and save memes — with AI-powered caption suggestions using Google Gemini, offline support, and Firebase-based authentication.

---

## ✨ Features

- 🔐 <strong>Authentication with Firebase</strong><br>
  Sign in using Email/Password or Google

- 🌐 <strong>Meme API Integration</strong><br>
  Fetches memes from the <a href="https://imgflip.com/api">Imgflip API</a>

- ✍️ <strong>Meme Editing</strong><br>
  Customize meme templates and add captions

- 🤖 <strong>AI Caption Generator</strong><br>
  Generate witty captions using Gemini AI

- 💾 <strong>Save Favorites</strong><br>
  Save memes internally to favorites and to local device storage

- 📶 <strong>Offline Support</strong><br>
  Caches memes locally using Room DB with refresh capability

- 🎨 <strong>Modern UI</strong><br>
  Built with Jetpack Compose, dark mode toggle, and responsive layout

---

## 🧰 Tech Stack

- <strong>Language</strong>: Kotlin  
- <strong>UI</strong>: Jetpack Compose + Material 3  
- <strong>Architecture</strong>: MVVM  
- <strong>Authentication</strong>: Firebase Auth (Email + Google)  
- <strong>Database</strong>: Room DB  
- <strong>Networking</strong>: Ktor Client  
- <strong>Image Loading</strong>: Coil  
- <strong>AI Integration</strong>: Google Gemini API  
- <strong>DI</strong>: Dagger-Hilt  
- <strong>Other</strong>: DataStore, Swipe Refresh, ExoPlayer (Media3)  

---

## 🚀 Getting Started

### 📦 Clone the Repository

```bash
git clone https://github.com/AryanPandeyDev/Meme-Forge.git
cd Meme-Forge
```

### 🛠️ Setup

1. Open the project in <strong>Android Studio Hedgehog</strong> or newer  
2. Create a <code>secret.properties</code> file and add your Gemini API key:

```properties
API_KEY=your_gemini_api_key:
```

3. Connect your Firebase project:
   - Enable <strong>Email</strong> and <strong>Google Sign-in</strong>  
   - Download <code>google-services.json</code>  
   - Place it inside the <code>/app</code> directory  

4. Sync Gradle and run the app on an emulator or real device

---

### 📸 Screenshots

<em>![HomeScreen](https://github.com/user-attachments/assets/2d406281-a299-4955-b7db-108af6037830)        ![WelcomeScreen](https://github.com/user-attachments/assets/df35dd3b-b8b4-44e8-8521-2af49ccdcb3d) 
![SignUpScreen](https://github.com/user-attachments/assets/f7172a9e-fd12-4103-9b00-bd3827bf57ba)    ![SlidingDrawerContents](https://github.com/user-attachments/assets/4ca648c6-23c0-4a27-9680-2bf3a973ad23)
![EditMemeScreen](https://github.com/user-attachments/assets/81793dfa-1791-4e65-84df-f3c0f8ca6253)  ![AiCaptionGenerator](https://github.com/user-attachments/assets/60c49f19-9697-47fd-8219-ba729c465314)
![FavouriteMemesScreen](https://github.com/user-attachments/assets/ab56190d-beac-4676-8706-b5f2df11d515)
</em><br>


---

### 📌 Folder Structure

```plaintext
com.example.memeforge/
├── authentication/         # Firebase Auth logic
├── data/, datastore/, db/  # Room DB, offline caching
├── network/                # Ktor API logic
├── screens/                # Compose UI screens
├── viewmodels/             # ViewModels for state mgmt
├── di/                     # Dagger-Hilt setup
├── utils/, theme/, etc.
```

---

### 📄 License

This project is licensed under the <a href="LICENSE">MIT License</a>.

---

### 🙌 Acknowledgements

🙌 Acknowledgements

- <a href="https://imgflip.com/api">Imgflip Meme API</a>  
- <a href="https://firebase.google.com/">Firebase by Google</a>  
- <a href="https://ai.google.dev/">Gemini AI</a>  
- <a href="https://developer.android.com/jetpack/compose">Jetpack Compose</a>  

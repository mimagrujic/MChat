# 📱 MChat – Android Messaging App

MChat is a real-time messaging application for Android, built entirely in **Java** using **Android Studio**, with **Firebase Realtime Database** integration. The app includes custom implementations for authentication, chat handling, and message-level encryption, with a focus on data security and clean UI interaction.

---

## 🚀 Features

- 🔐 **User Authentication**
  - Manual implementation of registration and login
  - Account deletion functionality
  - Passwords hashed using **scrypt** for strong protection

- 💬 **Chat Functionality**
  - Real-time messaging between users
  - Ability to delete individual messages or entire conversations
  - Smooth chat interface using **RecyclerView**

- 🔒 **Message Encryption**
  - Messages are encrypted using **AES-CBC** with a manually generated secret key
  - Encrypted data stored securely in Firebase
  - HMAC used for integrity verification

- ☁️ **Firebase Integration**
  - Firebase Realtime Database used for storing user data and messages
  - Real-time sync and updates across all connected devices

---

## 🛠️ Technologies Used

- **Java**
- **Android Studio**
- **Firebase Realtime Database**
- **AES-CBC encryption with HMAC**
- **scrypt password hashing**
- **RecyclerView** for chat display

---

## 📦 Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/mimagrujic/MChat.git
   ```

2. Open the project in Android Studio.

3. Connect your Firebase project:
   - Add `google-services.json` to the `app/` directory
   - Enable Firebase Realtime Database and Authentication

4. Run the app on an emulator or a physical device.

---

## 🔐 Security Notes

- Passwords are hashed with the **scrypt** key derivation function before storing in Firebase.
- Each message is encrypted using **AES in CBC mode** with a manually generated symmetric key.
- HMAC is used to ensure message integrity and detect tampering.

---

## 📍 Project Goals

This project was created to:
- Explore secure communication between users in a mobile app
- Gain hands-on experience with Firebase and custom authentication
- Understand the integration of cryptography in Android applications

---

## 📞 Contact

If you have questions or feedback, feel free to reach out via [LinkedIn](https://www.linkedin.com/in/mima-grujic/) or email.

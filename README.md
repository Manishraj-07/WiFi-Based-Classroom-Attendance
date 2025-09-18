# 📱 Wi-Fi Based Classroom Attendance App

A modern Android application that automates classroom attendance using Wi-Fi connectivity and Firebase backend. This app allows faculty to create attendance sessions and students to mark their presence automatically when connected to the classroom Wi-Fi network.

## 🎯 Project Overview

This project is a comprehensive attendance management system designed for educational institutions. It eliminates the need for manual attendance taking by leveraging Wi-Fi connectivity and real-time database synchronization.

### 🏆 Developed By
- **Manish Raj** 

## ✨ Key Features

### 👨‍🏫 Faculty Features
- **Secure Login**: Faculty authentication with email/password
- **Session Management**: Create and manage attendance sessions
- **Subject Selection**: Choose from available subjects
- **Branch & Section**: Select branch and section
- **Real-time Monitoring**: View attendance status in real-time
- **Session Control**: Start and end attendance sessions
- **Attendance Reports**: Generate and view detailed attendance reports
- **Logout Options**: Secure logout with session management

### 👨‍🎓 Student Features
- **Student Login**: Secure authentication for students
- **Wi-Fi Detection**: Automatic detection of classroom Wi-Fi
- **One-Click Attendance**: Simple attendance submission
- **Real-time Status**: Instant confirmation of attendance submission
- **Attendance History**: View personal attendance records
- **Session Validation**: Automatic validation of active sessions

### 🔧 Technical Features
- **Firebase Integration**: Real-time database and authentication
- **Offline Support**: Works with limited connectivity
- **Responsive UI**: Modern and intuitive user interface
- **Secure Authentication**: Firebase Auth with email/password
- **Real-time Sync**: Instant data synchronization
- **Error Handling**: Comprehensive error management

## 🛠️ Technology Stack

### Frontend
- **Language**: Java
- **Platform**: Android (API Level 21+)
- **UI Framework**: Android Views (XML Layouts)
- **Build Tool**: Gradle 8.5
- **JDK Version**: Java 17

### Backend
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication
- **Hosting**: Firebase Hosting
- **Configuration**: Google Services

## 📋 Prerequisites

Before running this application, ensure you have:

- **Android Studio**: Latest version (Hedgehog or newer)
- **JDK**: Java Development Kit 17 or higher
- **Android SDK**: API Level 21 (Android 5.0) or higher
- **Firebase Account**: Active Firebase project
- **Git**: For version control

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Manishraj-07/WiFi-Based-Classroom-Attendance.git
cd WiFi-Based-Classroom-Attendance
```

### 2. Firebase Setup

#### Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project named "WiFi-Based-Classroom-Attendance"
3. Enable Authentication and Realtime Database

#### Configure Authentication
1. In Firebase Console, go to Authentication > Sign-in method
2. Enable Email/Password authentication
3. Add the users:
**Faculty:**
**Students:**

#### Setup Realtime Database

1. Go to Realtime Database in Firebase Console
2. Create database in test mode
WiFi-Based-Classroom-Attendance/
├── Students/
├── Faculty/
├── Subjects/
└── AttendanceReport/

#### Download Configuration File
1. Go to Project Settings > General
2. Add Android app with package name: `com.example.wifibasedattendanceapplication`
3. Download `google-services.json`
4. Replace the existing file in `app/google-services.json`

### 3. Android Studio Setup
1. Open Android Studio
2. Open the project folder
3. Sync project with Gradle files
4. Install required SDK components if prompted

### 4. Build and Run
```bash
./gradlew assembleDebug
```
## 🏗️ Project Structure

```
app/
├── src/main/
│   ├── java/com/example/wifibasedattendanceapplication/
│   │   ├── AboutActivity.java              # About screen
│   │   ├── AttendanceReportActivity.java   # Attendance reports
│   │   ├── BaseAuthenticatedActivity.java  # Base authentication
│   │   ├── LoginActivity.java              # Main login screen
│   │   ├── LoginFacultyActivity.java       # Faculty login
│   │   ├── LoginStudentActivity.java       # Student login
│   │   ├── TakeAttendanceActivity.java     # Faculty attendance setup
│   │   ├── activity_session.java           # Attendance session management
│   │   ├── SubmitAttendanceActivity.java   # Student attendance submission
│   │   └── WifiAttendanceApplication.java  # Application class
│   ├── res/
│   │   ├── layout/                         # XML layouts
│   │   ├── drawable/                       # Images and icons
│   │   ├── values/                         # Strings, colors, themes
│   │   └── mipmap/                         # App icons
│   └── AndroidManifest.xml                 # App configuration
├── google-services.json                    # Firebase configuration
└── build.gradle                           # App-level build configuration
```

## 🔧 Configuration

### Firebase Security Rules
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

### Build Configuration
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34
- **Java Version**: 17

## 🐛 Troubleshooting

### Common Issues

1. **Build Errors**
   - Ensure JDK 17 is installed and configured
   - Check Android SDK installation
   - Verify Gradle version compatibility

2. **Firebase Connection Issues**
   - Verify `google-services.json` is in correct location
   - Check Firebase project configuration
   - Ensure internet connectivity

3. **Authentication Failures**
   - Verify user credentials in Firebase Console
   - Check email/password format
   - Ensure Firebase Auth is enabled

4. **Wi-Fi Detection Issues**
   - Check device Wi-Fi permissions
   - Verify network connectivity
   - Ensure stable internet connection

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java coding standards
- Add comments for complex logic
- Test on multiple Android versions
- Update documentation for new features

## 📞 Support

For support and questions:
- **Email**: manishraj5411@gmail.com
## 🎯 Future Enhancements

- [ ] Push notifications for attendance reminders
- [ ] Biometric authentication
- [ ] Offline mode with sync
- [ ] Advanced reporting and analytics
- [ ] Multi-language support
- [ ] Dark theme implementation
- [ ] QR code attendance option
- [ ] Integration with learning management systems

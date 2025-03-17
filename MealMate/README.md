# MealMate - Recipe and Shopping List Manager

MealMate is an Android application that helps users manage their recipes and shopping lists efficiently. The app allows users to create, store, and organize recipes while automatically generating shopping lists based on the ingredients needed.

## Features

### User Authentication
- User registration and login
- Secure password storage
- Profile management

### Recipe Management
- Create and edit recipes
- Add recipe photos
- Manage ingredient lists
- Store cooking instructions
- View recipe collection

### Shopping List
- Automatically generate shopping lists from recipes
- Mark items as purchased
- Share shopping lists via SMS
- Track shopping progress

### Profile Features
- View recipe statistics
- Update user information
- Track shopping history

## Technical Details

### Architecture
- MVVM architecture pattern
- Single Activity with multiple fragments
- Navigation Component for fragment management
- Room Database for local storage
- ViewBinding for view access
- Coroutines for asynchronous operations

### Libraries Used
- AndroidX Core KTX
- AndroidX AppCompat
- Material Design Components
- Navigation Component
- Room Database
- Kotlin Coroutines
- Glide for image loading
- ViewBinding
- LiveData and Flow
- ViewModel

### Requirements
- Android SDK 24 (Android 7.0) or higher
- Kotlin 1.9.20
- Gradle 8.2.0

## Setup Instructions

1. Clone the repository:
```bash
git clone https://github.com/yourusername/mealmate.git
```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Run the app on an emulator or physical device

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/mealmate/
│   │   │   ├── app/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── meals/
│   │   │   │   │   ├── shopping/
│   │   │   │   │   └── profile/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   ├── RegisterActivity.kt
│   │   │   │   └── HomeActivity.kt
│   │   │   └── data/
│   │   │       ├── model/
│   │   │       ├── dao/
│   │   │       └── MealMateDatabase.kt
│   │   └── res/
│   │       ├── layout/
│   │       ├── menu/
│   │       ├── navigation/
│   │       ├── values/
│   │       └── drawable/
│   └── androidTest/
└── build.gradle
```

## Features to be Added
- Recipe categories and tags
- Meal planning calendar
- Nutritional information
- Recipe sharing
- Multiple shopping lists
- Recipe scaling
- Favorite recipes
- Recipe search and filters

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

# KiotZ - Retail Management Android Application

A comprehensive retail management system built for Android that helps store managers and employees handle inventory, sales, and employee management efficiently.

## Features

### Authentication & User Management

- Firebase Authentication integration
- Role-based access control (Manager/Employee)
- User profile management
- Secure login and registration

### Inventory Management

- Product CRUD operations
- Barcode scanning functionality
- Image upload and management
- Real-time Stock tracking
- Category management
- Price management

### Sales & Receipt Management

- Generate and track receipts
- Daily/Weekly/Monthly sales statistics
- Revenue tracking
- Customer information management
- Invoice history

### Employee Management

- Employee registration and profile management
- Performance tracking
- Role assignment
- Activity monitoring

### Statistics & Analytics

- Sales performance metrics
- Employee performance tracking
- Revenue reports
- Product performance analysis
- Time-based filtering (Daily/Weekly/Monthly)

## Technical Stack

### Frontend

- Android SDK (min SDK 26)
- AndroidX components
- Material Design 3
- Glide for image loading
- Code Scanner for barcode scanning

### Backend

- Firebase Authentication
- Firebase Realtime Database
- Firebase Cloud Storage
- Firebase Cloud Functions

## Architecture

- MVVM (Model-View-ViewModel)
- Repository pattern
- Observer pattern with LiveData
- Dependency Injection

## Project Structure

## Setup & Installation

1. **Clone the repository**
2. **Set up Firebase Project**
   - Create a Firebase project
   - Add an Android app in Firebase console
   - Use your `google-services.json` and place it in the `app` directory
   - Enable Authentication and Realtime Database
3. **Open in Android Studio**
   - Open the project in Android Studio
   - Sync Gradle files
   - Build the project
4. **Run the application**
   - Connect an Android device or use an emulator
   - Run app configuration

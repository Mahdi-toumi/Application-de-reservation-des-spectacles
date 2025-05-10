# Coulisses - Spectacle Reservation App

## About
Coulisses is an Android mobile application developed by Mahdi Toumi at the National Engineering School of Carthage. It enables users to browse, search, and reserve tickets for cultural events and spectacles, featuring an intuitive Material Design interface, API-driven data retrieval, and seamless payment processing.

## Features
- **Spectacle Browsing**: View and filter spectacles by title, date, location, or price.
- **Detailed Information**: Access comprehensive details including descriptions, images, venue maps, and ticket availability.
- **Reservation System**: Select ticket types and quantities, with options for guest checkout or user authentication.
- **Secure Authentication**: Supports login, registration, and password reset via a REST API.
- **Payment & Confirmation**: Process payments and generate PDF receipts post-booking.
- **User-Friendly Interface**: Follows Material Design principles for intuitive navigation and accessibility.
- **Data Management**: Integrates with an Oracle database via Retrofit for dynamic content.

## Installation
1. **Prerequisites**:
   - Android Studio
   - Java Development Kit (JDK) 11 or higher
   - Android device/emulator (API 21 or higher)
2. **Setup**:
   - Clone the repository: `git clone https://github.com/yourusername/coulisses.git`
   - Open the project in Android Studio.
3. **Configuration**:
   - Update the API base URL in `ApiClient.java` (default: `http://192.168.1.11:8081/`).
   - Ensure dependencies (Retrofit, Glide, Gson) are included in `build.gradle`.
4. **Run**:
   - Build and run the app on an emulator or physical device.

## Project Structure
- **Network**:
  - `ApiClient.java`: Configures Retrofit for API communication.
  - `SpectacleApi.java`, `AuthApi.java`: Define API endpoints for spectacles and authentication.
- **Adapters**:
  - `SpectacleAdapter.java`: Manages dynamic spectacle display in RecyclerView.
- **Utils**:
  - `SessionManager.java`: Handles user session management with SharedPreferences.
- **Activities**: Contains UI components for splash, home, spectacle list, details, authentication, payment, and confirmation screens.
- **Models**: Defines data structures for spectacles, tickets, clients, and venues.

## Usage
1. Launch the app to view the splash screen, followed by the home page with featured spectacles.
2. Browse or search for spectacles by title, date, or location.
3. Select a spectacle to view details and check ticket availability.
4. Choose ticket types and quantities, then proceed to authenticate (login/register) or continue as a guest.
5. Enter payment details and confirm the booking to receive a PDF receipt.
6. Access reservation history via the user profile.

## Limitations
- Lacks a fully implemented backend for real-time reservation synchronization.
- Limited personalization features (e.g., no spectacle recommendations).
- Performance optimization needed for low-end devices.
- No multi-platform support (Android only).

## Future Improvements
- Develop a robust backend for real-time data synchronization.
- Implement personalized spectacle recommendations based on user preferences.
- Add push notifications for event updates and booking reminders.
- Optimize for low-performance devices with reduced image sizes.
- Extend to iOS using cross-platform frameworks like Flutter.

## Contributors
- **Mahdi Toumi**

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

## Acknowledgments
Developed as part of the Mobile Development course at the National Engineering School of Carthage, University of Carthage, for the academic year 2024-2025, under the supervision of Mme Olfa Lamouchi.
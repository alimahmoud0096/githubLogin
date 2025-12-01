# GitHub Repos Android App

A production-quality Android application that authenticates with GitHub and displays authenticated user repositories with branch information.

## Overview

This app demonstrates Clean Architecture principles with MVVM pattern, following Android best practices for:
- **Architecture**: Clean Architecture with clear layer separation
- **UI**: Jetpack Compose with Material Design 3
- **Dependency Injection**: Hilt
- **Networking**: Retrofit with Moshi
- **Security**: Encrypted SharedPreferences for token storage
- **Testing**: Unit tests for domain layer and ViewModels, UI tests

## Features

### Core Features
- ✅ GitHub OAuth authentication
- ✅ Paginated list of authenticated user's repositories
- ✅ Search/filter repositories by name
- ✅ Repository detail screen with branches (paginated)
- ✅ Sign out functionality

### Nice-to-Have Features
- ✅ Pull-to-refresh (via search functionality)
- ✅ Repository language chip
- ✅ Branch protection indicator

## Architecture

The app follows **Clean Architecture** with three main layers:

### Domain Layer
- **Entities**: `Repository`, `Branch`, `Owner`
- **Use Cases**: Business logic operations
  - `GetRepositoriesUseCase`
  - `SearchRepositoriesUseCase`
  - `GetBranchesUseCase`
  - `AuthenticateUseCase`
  - `SignOutUseCase`
  - `IsAuthenticatedUseCase`
- **Repository Interface**: `GitHubRepository`

### Data Layer
- **Repository Implementation**: `GitHubRepositoryImpl`
- **Remote Data Source**: `GitHubApi` (Retrofit interface)
- **Local Data Source**: `TokenStorage` (Encrypted SharedPreferences)
- **DTOs**: Data transfer objects for API responses
- **Mappers**: Convert DTOs to domain models

### Presentation Layer
- **ViewModels**: `AuthViewModel`, `RepositoriesViewModel`, `BranchesViewModel`
- **UI Screens**: Jetpack Compose screens
  - `SignInScreen`
  - `RepositoriesScreen`
  - `BranchesScreen`
- **Navigation**: Compose Navigation with type-safe routes

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK with API Level 35
- GitHub OAuth App credentials

### GitHub OAuth Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App
3. Set the Authorization callback URL to: `githubrepos://callback`
4. Copy the Client ID and Client Secret

### Configuration

1. Clone the repository
2. Create a `local.properties` file in the root directory (if it doesn't exist)
3. Add your GitHub OAuth credentials:

```properties
GITHUB_CLIENT_ID=your_client_id_here
GITHUB_CLIENT_SECRET=your_client_secret_here
```

Alternatively, you can add them to `gradle.properties`:

```properties
GITHUB_CLIENT_ID=your_client_id_here
GITHUB_CLIENT_SECRET=your_client_secret_here
```

### Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/githubrepos/app/
│   │   │   ├── data/              # Data layer
│   │   │   │   ├── local/          # Local data sources
│   │   │   │   ├── remote/        # Remote data sources
│   │   │   │   ├── mapper/        # Data mappers
│   │   │   │   └── repository/    # Repository implementations
│   │   │   ├── domain/            # Domain layer
│   │   │   │   ├── model/         # Domain entities
│   │   │   │   ├── repository/    # Repository interfaces
│   │   │   │   └── usecase/       # Use cases
│   │   │   ├── presentation/      # Presentation layer
│   │   │   │   ├── screen/        # Compose screens
│   │   │   │   ├── viewmodel/     # ViewModels
│   │   │   │   ├── navigation/    # Navigation
│   │   │   │   └── ui/theme/      # UI theme
│   │   │   ├── di/                # Dependency injection
│   │   │   └── GitHubReposApplication.kt
│   │   └── res/                    # Resources
│   ├── test/                       # Unit tests
│   └── androidTest/                # Instrumented tests
```

## Testing

### Unit Tests
Run unit tests with:
```bash
./gradlew test
```

Tests cover:
- Domain use cases
- ViewModels
- Repository implementations (via mocks)

### UI Tests
Run UI tests with:
```bash
./gradlew connectedAndroidTest
```

## Trade-offs and Design Decisions

### Architecture
- **Clean Architecture**: Chosen for separation of concerns and testability. Adds some boilerplate but improves maintainability.
- **MVVM**: Standard Android pattern that works well with Compose and state management.

### Security
- **Encrypted SharedPreferences**: Used for token storage instead of plain SharedPreferences for better security.
- **Token in Authorization Header**: Tokens are sent as `token <token>` in headers, following GitHub API best practices.

### Networking
- **Retrofit + Moshi**: Industry standard for type-safe API calls and JSON parsing.
- **Separate API instances**: Different Retrofit instances for GitHub API (`api.github.com`) and OAuth endpoint (`github.com`) due to different base URLs.
- **Form-encoded OAuth response**: GitHub's OAuth endpoint returns form-encoded data, handled with ScalarsConverterFactory.

### UI/UX
- **Material Design 3**: Modern design system with dynamic theming.
- **Pagination**: Implemented with automatic loading when scrolling to bottom.
- **Error Handling**: User-friendly error messages with retry functionality.
- **Loading States**: Clear loading indicators for better UX.

### Testing
- **Unit Tests**: Focus on domain logic and ViewModels where business logic resides.
- **UI Tests**: Basic smoke tests for critical user flows. More comprehensive UI tests would require more time.

## Future Improvements

### Short-term (Next Sprint)
1. **Offline Support**: Cache repositories and branches locally using Room database
2. **Pull-to-Refresh**: Add swipe-to-refresh gesture
3. **Deep Linking**: Support deep links to specific repositories
4. **Error Recovery**: Better handling of rate limiting with user-friendly messages
5. **Accessibility**: Add content descriptions and improve screen reader support

### Medium-term
1. **Repository Details**: Show more repository information (issues, pull requests count)
2. **Branch Details**: Show commit information for each branch
3. **Repository Actions**: Star/unstar, fork functionality
4. **Biometric Authentication**: Add biometric login option
5. **Dark Mode**: Enhanced dark theme support

### Long-term
1. **Multi-account Support**: Switch between multiple GitHub accounts
2. **Notifications**: GitHub notifications integration
3. **Repository Management**: Create, edit, delete repositories
4. **Code Viewing**: View file contents within the app
5. **Widgets**: Home screen widgets for quick repository access

## Known Issues

1. **OAuth Callback**: The OAuth callback handling could be improved with a more robust state management approach.
2. **Search Implementation**: Currently searches all repositories. Could be optimized to search only user's repositories more efficiently.
3. **Error Messages**: Some error messages could be more user-friendly and localized.

## Time Spent

Approximately **7-8 hours** of focused development time, including:
- Project setup and architecture: ~1 hour
- Domain and data layers: ~2 hours
- Presentation layer and UI: ~2.5 hours
- Testing: ~1.5 hours
- Documentation and polish: ~1 hour

## License

This project is created for evaluation purposes.


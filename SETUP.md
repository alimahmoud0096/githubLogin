# Quick Setup Guide

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- GitHub OAuth App

## Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd taskAndroid
   ```

2. **Create GitHub OAuth App**
   - Go to https://github.com/settings/developers
   - Click "New OAuth App"
   - Set Authorization callback URL: `githubrepos://callback`
   - Copy Client ID and Client Secret

3. **Configure OAuth Credentials**
   
   Create or edit `local.properties` in the root directory:
   ```properties
   GITHUB_CLIENT_ID=your_client_id_here
   GITHUB_CLIENT_SECRET=your_client_secret_here
   ```

   Or add to `gradle.properties`:
   ```properties
   GITHUB_CLIENT_ID=your_client_id_here
   GITHUB_CLIENT_SECRET=your_client_secret_here
   ```

4. **Open in Android Studio**
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

5. **Run the app**
   - Click Run or press Shift+F10
   - Select an emulator or connected device

## Testing

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### Build Errors
- Ensure JDK 17+ is set in Android Studio (File → Project Structure → SDK Location)
- Invalidate caches: File → Invalidate Caches / Restart

### OAuth Not Working
- Verify callback URL matches exactly: `githubrepos://callback`
- Check that Client ID and Secret are correctly set in `local.properties` or `gradle.properties`
- Ensure the app has internet permission (already configured in AndroidManifest.xml)

### Gradle Sync Issues
- Check internet connection (Gradle needs to download dependencies)
- Try: File → Sync Project with Gradle Files


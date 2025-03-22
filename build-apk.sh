
#!/bin/bash

echo "=== Setting up environment ==="
export JAVA_HOME=$(dirname $(dirname $(which java)))
export GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=-Xmx1536m -XX:MaxMetaspaceSize=384m"

# Check if android directory exists
if [ ! -d "android" ]; then
  echo "Error: android directory not found. Are you in the right directory?"
  exit 1
fi

# Skip buildSrc and compile only android-specific modules
echo "=== Creating android-only build configuration ==="
echo "android.buildOnlyTargetSdk=true" >> gradle.properties
echo "org.gradle.configureondemand=true" >> gradle.properties

echo "=== Step 1: Clean project ==="
./gradlew clean --no-daemon --console=plain --exclude-task test --exclude-task lint

echo "=== Step 2: Build APK (Debug mode) ==="
cd android
../gradlew --no-daemon --console=plain \
  -Dorg.gradle.parallel=true \
  -Dorg.gradle.caching=true \
  -Dkotlin.compiler.execution.strategy=in-process \
  -Dorg.gradle.workers.max=2 \
  -Pandroid.optional.compilation=INSTANT_DEV \
  -Pkotlin.incremental=false \
  --exclude-task test \
  --exclude-task lint \
  app:assembleDebug
cd ..

# Check if build was successful
if [ $? -eq 0 ]; then
  echo "=== Build complete! ==="
  APK_PATH="android/app/build/outputs/apk/debug/app-debug.apk"
  if [ -f "$APK_PATH" ]; then
    echo "APK successfully built at: $APK_PATH"
    ls -lh "$APK_PATH"
  else
    echo "Error: APK file not found at expected location: $APK_PATH"
    echo "Searching for APK files:"
    find android -name "*.apk" -type f
  fi
else
  echo "=== Build failed ==="
  echo "Try building directly from the android directory:"
  echo "cd android && ../gradlew app:assembleDebug"
fi

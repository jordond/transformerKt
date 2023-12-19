#!/bin/bash

CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
PARENT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

set -e

SEMVER_REG="([[:digit:]]+(\.[[:digit:]]+)+)"

README_FILE="$PARENT/README.md"
VERSION_FILE="$PARENT/gradle/libs.versions.toml"

NEW_VERSION="$ORG_GRADLE_PROJECT_VERSION_NAME"
if [ -z "$NEW_VERSION" ]; then
  NEW_VERSION="$1"
  if [ -n "$NEW_VERSION" ]; then
    echo "Update README with version: '$NEW_VERSION'"

    if [[ "$OSTYPE" == "darwin"* ]]; then
      # Update artifact versions in README.md
      sed -i '' -E "s/\:$SEMVER_REG\"\)/\:$NEW_VERSION\"\)/" "$README_FILE"

      # Update version catalog in README.md
      sed -i '' -E "s/transformerkt = \"$SEMVER_REG\"/transformerkt = \"$NEW_VERSION\"/" "$README_FILE"
    else
      sed -i -E "s/\:$SEMVER_REG\"/\:$NEW_VERSION\"/g" "$README_FILE"
      sed -i -E "s/transformerkt = \"$SEMVER_REG\"/transformerkt = \"$NEW_VERSION\"/g" "$README_FILE"
    fi
  fi
fi

# Update Kotlin badge in README.md
LIBS_KOTLIN_VERSION=$(grep "kotlin = " "$VERSION_FILE" | cut -d= -f2 | tr -d ' "')
if [ -z "$LIBS_KOTLIN_VERSION" ]; then
  echo "Unable to find Kotlin version in '$VERSION_FILE'"
else
  echo "Updating Kotlin version: '$LIBS_KOTLIN_VERSION'"
  sed -i '' -E "s/kotlin-v$SEMVER_REG/kotlin-v$LIBS_KOTLIN_VERSION/" "$README_FILE"
fi

# Update Media3 badge in README.md
LIBS_MEDIA3_VERSION=$(grep "androidx-media3 = " "$VERSION_FILE" | cut -d= -f2 | tr -d ' "')
if [ -z "$LIBS_MEDIA3_VERSION" ]; then
  echo "Unable to find Media3 version in '$VERSION_FILE'"
else
  echo "Updating Media3 version: '$LIBS_MEDIA3_VERSION'"
  sed -i '' -E "s/media3#$SEMVER_REG/media3#$LIBS_MEDIA3_VERSION/" "$README_FILE"
  sed -i '' -E "s/media3-#$SEMVER_REG/media3-#$LIBS_MEDIA3_VERSION/" "$README_FILE"
  sed -i '' -E "s/version%20\`$SEMVER_REG/version%20\`$LIBS_MEDIA3_VERSION/" "$README_FILE"
fi

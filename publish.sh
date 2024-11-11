#!/bin/bash

version="$1"

note_file="/tmp/inject-release-notes.md"
nvim "$note_file"
notes=$(cat "$note_file")

for dir in *; do
  [[ -f "$dir/build.gradle.kts" ]] && ./gradlew "$dir:publish"
done

gh release create "v$version" -F note_file

publish_version() {
  loader="$1"
  api_file="inject-api-$version.jar=@api/build/libs/api-$version.jar"
  loader_file="inject-$loader-$version.jar=@$loader/build/libs/$loader-$version.jar"
  versions='["1.21.1", "1.21.2", "1.21.3"]'

  curl \
    -X POST \
    -H "Authorization: Bearer $MODRINTH_TOKEN" \
    -F"$api_file" \
    -F"$loader_file" \
    --body "
    {
      \"name\": \"Inject $version\",
      \"version_number\": \"$version\",
      \"changelog\": \"$notes\",
      \"game_versions\": $versions,
      \"version_type\": \"release\",
      \"loaders\": [\"$loader\"],
      \"status\": \"listed\",
      \"requested_status\": \"listed\",
      \"project_id\": \"Cd6cEGUq\",
      \"file_parts\": [\"inject-$loader-$version.jar\", \"inject-api-$version.jar\"],
      \"primary_file\": \"inject-$loader-$version.jar\"
    }
  " \
    https://api.modrinth.com/v2/version
}

publish_version fabric
publish_version spigot
publish_version paper

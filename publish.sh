#!/bin/bash

# Maven, GH and Modrinth publishing script made by rad.
# You may have to tweak this to your environment for it to work.
# Requirements:
# * Environment variable `MODRINTH_TOKEN`
# * Environment variable `EDITOR`
# * GH CLI
# * cURL

version="$1"
note_file="/tmp/inject-release-notes.md"
"$EDITOR" "$note_file"
notes=$(cat "$note_file")

for dir in *; do
  [[ -f "$dir/build.gradle.kts" ]] && ./gradlew "$dir:publish"
done

gh release create "v$version" -F "$note_file"

publish_version() {
  loader="$1"

  api_file="api/build/libs/inject-api-$version.jar"

  if [[ "$loader" == "paper" ]]; then
    loader_file="paper/build/libs/inject-paper-$version-dev.jar"
  else
    loader_file="$loader/build/libs/inject-$loader-$version.jar"
  fi

  if [[ ! -f "$api_file" || ! -f "$loader_file" ]]; then
    echo "One or more files not found: $api_file, $loader_file"
    return 1
  fi

  changelog=$(echo "$notes" | sed 's/"/\\"/g')
  game_versions='["1.21.1", "1.21.2", "1.21.3"]'

  version_data=$(cat <<EOF
    {
      "name": "Inject $version",
      "version_number": "$version",
      "changelog": "$changelog",
      "game_versions": $game_versions,
      "version_type": "release",
      "loaders": ["$loader"],
      "status": "listed",
      "requested_status": "listed",
      "project_id": "Cd6cEGUq",
      "file_parts": ["inject-$loader-$version.jar", "inject-api-$version.jar"],
      "primary_file": "inject-$loader-$version.jar",
      "dependencies": [],
      "featured": false
    }
EOF
)

  curl \
    -X POST \
    -H "Authorization: Bearer $MODRINTH_TOKEN" \
    -H "Content-Type: multipart/form-data" \
    -H "User-Agent: mcbrawls/inject/$version (contact@mcbrawls.net)" \
    -F"data=$version_data" \
    -F"file=@$api_file" \
    -F"file=@$loader_file" \
    https://api.modrinth.com/v2/version
}

publish_version fabric
publish_version spigot
publish_version paper

rm "$note_file"
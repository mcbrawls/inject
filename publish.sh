#!/bin/bash

for dir in *; do
  [[ -f "$dir/build.gradle.kts" ]] && ./gradlew "$dir:publish"
done
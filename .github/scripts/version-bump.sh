#!/bin/bash
set -euo pipefail

SEMVER_REGEX="^([0-9]+)\.([0-9]+)\.([0-9]+)(-([a-zA-Z][0-9a-zA-Z]*)\.([0-9]+))?(-SNAPSHOT)?(\+[0-9a-zA-Z-]+(\.[0-9a-zA-Z-]+)*)?$"

if [[ $# -ne 2 ]]; then
  echo "::error::Usage: $0 <version_file> <current_version>" >&2
  exit 1
fi

version_file="$1"
prev_version="$2"

if [[ ! -f "$version_file" ]]; then
  echo "::error::Version file '$version_file' not found" >&2
  exit 1
fi

if [[ ! "$prev_version" =~ $SEMVER_REGEX ]]; then
  echo "::error::Version '$prev_version' does not match semver regex '$SEMVER_REGEX'" >&2
  exit 1
fi

major="${BASH_REMATCH[1]}"
minor="${BASH_REMATCH[2]}"
patch="${BASH_REMATCH[3]}"
extra="${BASH_REMATCH[5]}"
build="${BASH_REMATCH[6]}"

if [[ -n "$extra" ]]; then
  next_version="$major.$minor.$patch-$extra.$((build + 1))"
else
  next_version="$major.$minor.$((patch + 1))"
fi

sed -i "s/$prev_version/$next_version/g" "$version_file"

echo "::notice::Version bumped: $prev_version -> $next_version"

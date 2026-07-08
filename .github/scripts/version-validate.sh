#!/bin/bash
set -euo pipefail

SEMVER_REGEX="^([0-9]+)\.([0-9]+)\.([0-9]+)(-([a-zA-Z][0-9a-zA-Z]*)\.([0-9]+))?(-SNAPSHOT)?(\+[0-9a-zA-Z-]+(\.[0-9a-zA-Z-]+)*)?$"

if [[ $# -ne 2 ]]; then
  echo "::error::Usage: $0 <version> <release_mode>" >&2
  exit 1
fi

version="$1"
release_mode="$2"

if [[ ! "$version" =~ $SEMVER_REGEX ]]; then
  echo "::error::Version is invalid, expected matching '$SEMVER_REGEX', got '$version'" >&2
  exit 1
fi

if [[ "$release_mode" == "true" ]]; then
  if [[ "$version" == *"-SNAPSHOT" ]]; then
    echo "::error::-SNAPSHOT suffix is present in release mode" >&2
    exit 1
  fi
else
  git_version="v${version%-SNAPSHOT}"
  if [[ "$(git tag -l "$git_version")" != "" ]]; then
    echo "::error::'$git_version' already exists as a git tag" >&2
    exit 1
  fi
fi

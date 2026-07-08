#!/bin/bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "::error::Usage: $0 <github_repository> <output_file>" >&2
  exit 1
fi

github_repository="$1"
output_file="$2"
latest_tag=$(git tag --sort=-creatordate | head -n 1)
query="${latest_tag:+$latest_tag..}HEAD"
work_dir=$(mktemp -d)
trap 'rm -rf "$work_dir"' EXIT

git log "$query" --format="%H|%h|%s" | while IFS='|' read -r sha short_sha summary_raw; do
  category="misc"
  summary="$summary_raw"

  if [[ "$summary_raw" =~ ^([a-zA-Z]+)(\([a-zA-Z0-9_-]+\))?!?:\ (.*)$ ]]; then
    category_raw="${BASH_REMATCH[1],,}"
    summary="${BASH_REMATCH[3]}"

    case "$category_raw" in
      feat)
        category="feat"   ;;
      fix)
        category="fix"    ;;
      chore|docs|refactor|style|test)
        category="chore"  ;;
      *)
        category="misc"   ;;
    esac
  else
    if [[ "${summary,,}" = "fix"* ]]; then
      category="fix"
    fi
  fi

  url="https://github.com/$github_repository/commit/$sha"
  echo "- $summary ([\`$short_sha\`]($url))" >> "$work_dir/$category"
done

echo_section() {
  if [[ -f "$work_dir/$1" ]]; then
    echo "### $2"
    echo
    cat "$work_dir/$1"
    echo
  fi
}

{
  echo_section "feat"   "Changes & New features"
  echo_section "fix"    "Bugfixes"
  echo_section "chore"  "Maintenance"
  echo_section "misc"   "Miscellaneous"
} > "$output_file"

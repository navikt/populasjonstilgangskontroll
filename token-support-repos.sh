#!/usr/bin/env bash

set -euo pipefail

usage() {
  echo "Usage: $0 <org> [artifact-pattern]" >&2
  echo "Example: $0 navikt token-support" >&2
}

if [[ $# -lt 1 || $# -gt 2 ]]; then
  usage
  exit 1
fi

org="$1"
pattern="${2:-token-support}"

filenames=(
  "build.gradle"
  "build.gradle.kts"
  "libs.versions.toml"
  "pom.xml"
)

matches_file="$(mktemp)"
repos_file="$(mktemp)"
trap 'rm -f "$matches_file" "$repos_file"' EXIT

for filename in "${filenames[@]}"; do
  gh search code "$pattern" \
    --owner "$org" \
    --filename "$filename" \
    --match file \
    --limit 1000 \
    --json repository,path \
    --jq '.[] | [.repository.nameWithOwner, .path] | @tsv'
done | sort -u > "$matches_file"

cut -f1 "$matches_file" | sort -u > "$repos_file"

repo_count="$(wc -l < "$repos_file" | tr -d ' ')"

echo "Repositories in ${org} using pattern '${pattern}': ${repo_count}"
echo
echo "Repository names:"
cat "$repos_file"
echo
echo "Matches by repository:"

awk -F '\t' '
  {
    if ($1 != current && current != "") {
      print current ": " paths
      paths = ""
    }
    current = $1
    paths = paths ? paths ", " $2 : $2
  }
  END {
    if (current != "") {
      print current ": " paths
    }
  }
' "$matches_file"

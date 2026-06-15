#!/usr/bin/env bash
# Compile and run the inventory/order demo.
#
# The source files declare `package collections;` but live in the repo root,
# so we stage them into a matching `collections/` directory before compiling.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD="${ROOT}/build"
SRC="${BUILD}/src/collections"
OUT="${BUILD}/out"

rm -rf "${BUILD}"
mkdir -p "${SRC}" "${OUT}"
cp "${ROOT}"/*.java "${SRC}/"

echo "=== Compiling ==="
javac -d "${OUT}" "${SRC}"/*.java
echo "=== Running collections.Demo ==="
java -cp "${OUT}" collections.Demo

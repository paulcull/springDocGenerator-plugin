#!/bin/bash

# Builds and installs the SpringDoc SDK Generator plugin suite locally,
# including downloading required Bootstrap assets.

# --- Configuration ---
BOOTSTRAP_VERSION="5.3.3"
BOOTSTRAP_CSS_URL="https://cdn.jsdelivr.net/npm/bootstrap@${BOOTSTRAP_VERSION}/dist/css/bootstrap.min.css"
BOOTSTRAP_JS_URL="https://cdn.jsdelivr.net/npm/bootstrap@${BOOTSTRAP_VERSION}/dist/js/bootstrap.bundle.min.js"

# --- Script Start ---

# Get the directory where the script resides (project root)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
ASSETS_DIR="${SCRIPT_DIR}/core/src/main/resources/static-docs-assets"

# Change to the script's directory
cd "$SCRIPT_DIR" || exit 1

echo "================================================="
echo "Preparing SpringDoc SDK Generator Plugin Assets..."
echo "Target Directory: ${ASSETS_DIR}"
echo "================================================="

# Create asset directory if it doesn't exist
mkdir -p "${ASSETS_DIR}"
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create asset directory: ${ASSETS_DIR}" >&2
    exit 1
fi

# Download Bootstrap CSS
echo "Downloading Bootstrap v${BOOTSTRAP_VERSION} CSS from ${BOOTSTRAP_CSS_URL}..."
curl -fL "${BOOTSTRAP_CSS_URL}" -o "${ASSETS_DIR}/bootstrap.min.css"
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to download Bootstrap CSS." >&2
    exit 1
fi

# Download Bootstrap JS
echo "Downloading Bootstrap v${BOOTSTRAP_VERSION} JS from ${BOOTSTRAP_JS_URL}..."
curl -fL "${BOOTSTRAP_JS_URL}" -o "${ASSETS_DIR}/bootstrap.bundle.min.js"
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to download Bootstrap JS." >&2
    exit 1
fi

echo "Bootstrap assets download complete."


echo "================================================="
echo "Building and Installing SpringDoc SDK Generator Plugin..."
echo "================================================="

# Run Maven clean install, skipping tests for faster installation
# mvn clean install -DskipTests
mvn clean install package

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
  echo "================================================="
  echo "Plugin installation successful!"
  echo "================================================="
else
  echo "================================================="
  echo "Plugin installation FAILED! Exit code: $EXIT_CODE"
  echo "================================================="
fi

exit $EXIT_CODE 
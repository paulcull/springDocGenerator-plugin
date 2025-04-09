@echo off
REM Builds and installs the SpringDoc SDK Generator plugin suite locally,
REM including downloading required Bootstrap assets.

REM --- Configuration ---
set BOOTSTRAP_VERSION=5.3.3
set BOOTSTRAP_CSS_URL=https://cdn.jsdelivr.net/npm/bootstrap@%BOOTSTRAP_VERSION%/dist/css/bootstrap.min.css
set BOOTSTRAP_JS_URL=https://cdn.jsdelivr.net/npm/bootstrap@%BOOTSTRAP_VERSION%/dist/js/bootstrap.bundle.min.js

REM --- Script Start ---

REM Assumes script is run from the project root directory
set SCRIPT_DIR=%~dp0
REM Construct asset path relative to script directory
set ASSETS_DIR=%SCRIPT_DIR%core\src\main\resources\static-docs-assets

echo =================================================
echo Preparing SpringDoc SDK Generator Plugin Assets...
echo Target Directory: %ASSETS_DIR%
echo =================================================

REM Create asset directory if it doesn't exist
if not exist "%ASSETS_DIR%" (
    mkdir "%ASSETS_DIR%"
    if %ERRORLEVEL% neq 0 (
        echo ERROR: Failed to create asset directory: %ASSETS_DIR%
        exit /b 1
    )
)

REM Download Bootstrap CSS (Requires curl.exe in PATH)
echo Downloading Bootstrap v%BOOTSTRAP_VERSION% CSS from %BOOTSTRAP_CSS_URL%...
curl -fL "%BOOTSTRAP_CSS_URL%" -o "%ASSETS_DIR%\bootstrap.min.css"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to download Bootstrap CSS. Make sure curl.exe is in your PATH. >&2
    exit /b 1
)

REM Download Bootstrap JS (Requires curl.exe in PATH)
echo Downloading Bootstrap v%BOOTSTRAP_VERSION% JS from %BOOTSTRAP_JS_URL%...
curl -fL "%BOOTSTRAP_JS_URL%" -o "%ASSETS_DIR%\bootstrap.bundle.min.js"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Failed to download Bootstrap JS. Make sure curl.exe is in your PATH. >&2
    exit /b 1
)

echo Bootstrap assets download complete.

echo.
echo =================================================
echo Building and Installing SpringDoc SDK Generator Plugin...
echo =================================================

REM Run Maven clean install, skipping tests for faster installation
REM Use 'call' to ensure control returns to the script
call mvn clean install -DskipTests
set EXIT_CODE=%ERRORLEVEL%

if %EXIT_CODE% equ 0 (
  echo =================================================
  echo Plugin installation successful!
  echo =================================================
) else (
  echo =================================================
  echo Plugin installation FAILED! Exit code: %EXIT_CODE%
  echo =================================================
)

exit /b %EXIT_CODE% 
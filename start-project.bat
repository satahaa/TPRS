@echo off
REM TPRS Project Startup Script (Batch Version)
REM This script builds and runs both the backend and frontend

echo ========================================
echo    TPRS - Project Startup Script
echo ========================================
echo.

REM Configuration
set "PROJECT_ROOT=%~dp0"
set "BACKEND_DIR=%PROJECT_ROOT%backend"
set "MAVEN_PATH=%USERPROFILE%\.maven\maven-3.9.12\bin"

REM Add Maven to PATH
set "PATH=%MAVEN_PATH%;%PATH%"

REM Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven not found. Please install Maven first.
    pause
    exit /b 1
)
echo [OK] Maven found

REM Step 1: Build the backend
echo.
echo [1/3] Building the backend...
cd /d "%BACKEND_DIR%"
call mvn clean package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Backend build failed!
    pause
    exit /b 1
)
echo [OK] Backend built successfully!

REM Step 2: Start the frontend server in background
echo.
echo [2/3] Starting frontend server on port 3000...
cd /d "%PROJECT_ROOT%"
start "TPRS Frontend" cmd /c "python -m http.server 3000"
timeout /t 2 >nul
echo [OK] Frontend server started at http://localhost:3000

REM Step 3: Start the backend server
echo.
echo [3/3] Starting backend server on port 8080...
echo.
echo ========================================
echo    Project is now running!
echo ========================================
echo.
echo Frontend:  http://localhost:3000/home.html
echo Backend:   http://localhost:8080/tprs/api
echo.
echo Close this window to stop the backend server
echo Close the other window to stop the frontend server
echo.

cd /d "%BACKEND_DIR%"
call mvn jetty:run

pause

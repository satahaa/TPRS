# TPRS Project Startup Script
# This script builds and runs both the backend and frontend

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   TPRS - Project Startup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$projectRoot = $PSScriptRoot
$backendDir = Join-Path $projectRoot "backend"
$mavenPath = "$env:USERPROFILE\.maven\maven-3.9.12\bin"

# Add Maven to PATH if not already available
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    if (Test-Path $mavenPath) {
        $env:PATH = "$mavenPath;$env:PATH"
        Write-Host "[OK] Maven added to PATH" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Maven not found. Please install Maven first." -ForegroundColor Red
        exit 1
    }
}

# Step 1: Build the backend
Write-Host ""
Write-Host "[1/3] Building the backend..." -ForegroundColor Yellow
Set-Location $backendDir

$buildResult = & mvn clean package -DskipTests 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Backend build failed!" -ForegroundColor Red
    Write-Host $buildResult
    exit 1
}
Write-Host "[OK] Backend built successfully!" -ForegroundColor Green

# Step 2: Start the frontend server in background
Write-Host ""
Write-Host "[2/3] Starting frontend server on port 3000..." -ForegroundColor Yellow
Set-Location $projectRoot

$frontendJob = Start-Job -ScriptBlock {
    param($path)
    Set-Location $path
    python -m http.server 3000
} -ArgumentList $projectRoot

Start-Sleep -Seconds 2
Write-Host "[OK] Frontend server started at http://localhost:3000" -ForegroundColor Green

# Step 3: Start the backend server
Write-Host ""
Write-Host "[3/3] Starting backend server on port 8080..." -ForegroundColor Yellow
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Project is now running!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Frontend:  http://localhost:3000/home.html" -ForegroundColor White
Write-Host "Backend:   http://localhost:8080/tprs/api" -ForegroundColor White
Write-Host ""
Write-Host "Press Ctrl+C to stop the servers" -ForegroundColor Gray
Write-Host ""

Set-Location $backendDir

# Add Maven to PATH again for this command
$env:PATH = "$mavenPath;$env:PATH"

try {
    & mvn jetty:run
} finally {
    # Cleanup: Stop the frontend job when backend stops
    Write-Host ""
    Write-Host "Stopping frontend server..." -ForegroundColor Yellow
    Stop-Job -Job $frontendJob -ErrorAction SilentlyContinue
    Remove-Job -Job $frontendJob -ErrorAction SilentlyContinue
    Write-Host "Servers stopped." -ForegroundColor Green
}
